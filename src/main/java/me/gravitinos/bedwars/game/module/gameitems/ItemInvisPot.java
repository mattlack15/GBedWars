package me.gravitinos.bedwars.game.module.gameitems;

import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.game.info.BWPlayerInfo;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.bedwars.gamecore.gameitem.SimpleGameItemHandler;
import me.gravitinos.bedwars.gamecore.scoreboard.ModuleScoreboard;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import me.gravitinos.bedwars.gamecore.util.PacketEvent;
import me.gravitinos.bedwars.gamecore.util.TextUtil;
import net.minecraft.server.v1_12_R1.EnumItemSlot;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

public class ItemInvisPot extends SimpleGameItemHandler {

    private static final int durationSeconds = 30;

    private static final String itemIdentifier = ChatColor.translateAlternateColorCodes('&', "&f&lInvisibility &e(" + durationSeconds + " seconds)");

    private ArrayList<UUID> invisible = new ArrayList<>();
    private ArrayList<Integer> invisibleEID = new ArrayList<>();

    public void removeInvisible(@NotNull Player p) {
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        this.restoreArmor(p);

        BedwarsHandler handler = (BedwarsHandler) getModule().getGameHandler();
        BWPlayerInfo playerInfo = handler.getPlayerInfo(p.getUniqueId());
        handler.getModule(ModuleScoreboard.class).getScoreboard().getTeam(playerInfo.getTeam().getName()).addEntry(playerInfo.getName());

        this.invisible.remove(p.getUniqueId());
        this.invisibleEID.remove((Integer)p.getEntityId());
    }

    public void setInvisible(@NotNull Player player, int durationSeconds) {
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,durationSeconds * 20,0,false,false));
        this.invisibleEID.add(player.getEntityId());
        this.invisible.add(player.getUniqueId());

        UUID id = player.getUniqueId();

        new BukkitRunnable(){
            @Override
            public void run() {
                if(Bukkit.getPlayer(id) != null){
                    if(!Bukkit.getPlayer(id).hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                        removeInvisible(Bukkit.getPlayer(id));
                    }
                }
            }
        }.runTaskLater(CoreHandler.main, durationSeconds * 20 + 1);

    }

    public ItemInvisPot(@NotNull ModuleGameItems module) {
        super(module, BedwarsItem.ITEM_INVIS_POT.toString(), itemIdentifier, null);
    }

    public ArrayList<UUID> getInvisiblePlayers() {
        return invisible;
    }

    @Override
    public String getDescription() {
        return "Will turn you invisible for 30 seconds";
    }

    @Override
    public ItemStack getItem(int level) {
        ItemBuilder builder = new ItemBuilder(Material.POTION, 1).setName(itemIdentifier);
        builder.clearLore();
        for (String lines : TextUtil.splitIntoLines(this.getDescription(), 25)) {
            builder.addLore(ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', lines));
        }
        PotionMeta meta = (PotionMeta) builder.getItemMeta();
        meta.setColor(Color.WHITE);
        meta.setBasePotionData(new PotionData(PotionType.INVISIBILITY));
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        builder.setItemMeta(meta);
        return builder.build();
    }

    @EventSubscription
    private void onConsume(PlayerItemConsumeEvent event) {
        if (!getModule().getGameHandler().isPlaying(event.getPlayer().getUniqueId())) {
            return;
        }
        if (this.isMatch(event.getItem())) {
            this.sendPacketNoArmor(event.getPlayer());
            event.setItem(new ItemStack(Material.AIR));
            BedwarsHandler handler = (BedwarsHandler) getModule().getGameHandler();

            this.setInvisible(event.getPlayer(), durationSeconds);

        }
    }

    @EventSubscription
    private void onPacketEquipment(PacketEvent<PacketPlayOutEntityEquipment> event) {
        PacketPlayOutEntityEquipment entityEquipment = event.getPacket();
        try {

            Field EID = entityEquipment.getClass().getDeclaredField("a");
            EID.setAccessible(true);
            int id = (int)EID.get(entityEquipment);
            EID.setAccessible(false);

            if(!this.invisibleEID.contains(id)){
                return;
            }

            Field slotField = entityEquipment.getClass().getDeclaredField("b");
            slotField.setAccessible(true);
            Enum slot = (Enum) slotField.get(entityEquipment);
            slotField.setAccessible(false);

            if (slot.ordinal() < 2) {
                return;
            }

            Field field = entityEquipment.getClass().getDeclaredField("c");
            field.setAccessible(true);
            field.set(entityEquipment, CraftItemStack.asNMSCopy(null));
            field.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void sendPacketNoArmor(Player p) {
        PacketPlayOutEntityEquipment equipmentFeet = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.FEET, CraftItemStack.asNMSCopy(null));
        PacketPlayOutEntityEquipment equipmentLegs = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(null));
        PacketPlayOutEntityEquipment equipmentChest = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(null));
        PacketPlayOutEntityEquipment equipmentHead = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(null));

        for (Player players : Bukkit.getOnlinePlayers()) {

            if (players.getUniqueId().equals(p.getUniqueId())) continue;

            ((CraftPlayer) players).getHandle().playerConnection.sendPacket(equipmentFeet);
            ((CraftPlayer) players).getHandle().playerConnection.sendPacket(equipmentLegs);
            ((CraftPlayer) players).getHandle().playerConnection.sendPacket(equipmentChest);
            ((CraftPlayer) players).getHandle().playerConnection.sendPacket(equipmentHead);
        }
    }

    public void restoreArmor(Player p) {
        PacketPlayOutEntityEquipment equipmentFeet = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.FEET, CraftItemStack.asNMSCopy(p.getInventory().getBoots()));
        PacketPlayOutEntityEquipment equipmentLegs = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(p.getInventory().getLeggings()));
        PacketPlayOutEntityEquipment equipmentChest = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(p.getInventory().getChestplate()));
        PacketPlayOutEntityEquipment equipmentHead = new PacketPlayOutEntityEquipment(p.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(p.getInventory().getHelmet()));

        for (Player players : Bukkit.getOnlinePlayers()) {

            if (players.getUniqueId().equals(p.getUniqueId())) continue;

            ((CraftPlayer) players).getHandle().playerConnection.sendPacket(equipmentFeet);
            ((CraftPlayer) players).getHandle().playerConnection.sendPacket(equipmentLegs);
            ((CraftPlayer) players).getHandle().playerConnection.sendPacket(equipmentChest);
            ((CraftPlayer) players).getHandle().playerConnection.sendPacket(equipmentHead);
        }
    }
}
