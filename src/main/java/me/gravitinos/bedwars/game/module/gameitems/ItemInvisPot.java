package me.gravitinos.bedwars.game.module.gameitems;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.game.info.BWPlayerInfo;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.bedwars.gamecore.gameitem.SimpleGameItemHandler;
import me.gravitinos.bedwars.gamecore.scoreboard.ModuleScoreboard;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.UUID;

public class ItemInvisPot extends SimpleGameItemHandler {

    private static final int durationSeconds = 30;

    private static final String itemIdentifier = ChatColor.translateAlternateColorCodes('&', "&f&lInvisibility &e(" + durationSeconds + " seconds)");

    private ArrayList<UUID> invisible = new ArrayList<>();
    private ArrayList<Integer> invisibleEID = new ArrayList<>();

    public void removeInvisible(@NotNull Player p) {
        if(!this.isEnabled()){
            return;
        }
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        this.restoreArmor(p);

        BedwarsHandler handler = (BedwarsHandler) getModule().getGameHandler();
        BWPlayerInfo playerInfo = handler.getPlayerInfo(p.getUniqueId());
        handler.getModule(ModuleScoreboard.class).getScoreboard().getTeam(playerInfo.getTeam().getName()).addEntry(playerInfo.getName());

        this.invisible.remove(p.getUniqueId());
        this.invisibleEID.remove((Integer)p.getEntityId());
    }

    public void setInvisible(@NotNull Player player, int durationSeconds) {
        if(!this.isEnabled()){
            return;
        }
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

    private PacketAdapter packetAdapter = new PacketAdapter(CoreHandler.main, PacketType.Play.Server.ENTITY_EQUIPMENT) {
        @Override
        public void onPacketSending(PacketEvent event) {
            if(!event.getPacketType().equals(PacketType.Play.Server.ENTITY_EQUIPMENT))
                return;

            int id = event.getPacket().getIntegers().read(0);

            if(!invisibleEID.contains(id)){
                return;
            }

            EnumWrappers.ItemSlot slot = event.getPacket().getItemSlots().read(0);

            if (slot.ordinal() < 2) {
                return;
            }

            event.getPacket().getItemModifier().write(0, null);
        }
    };

    @Override
    public void enable(){
        super.enable();
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        pm.addPacketListener(packetAdapter);
    }

    @Override
    protected void disable() {
        super.disable();
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        pm.removePacketListener(packetAdapter);
    }

    public void sendPacketNoArmor(Player p) {

        ProtocolManager pm = ProtocolLibrary.getProtocolManager();

        PacketContainer pc = pm.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        pc.getIntegers().write(0, p.getEntityId());
        pc.getItemSlots().write(0, EnumWrappers.ItemSlot.FEET);
        pc.getItemModifier().write(0, null);

        PacketContainer pc1 = pm.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        pc1.getIntegers().write(0, p.getEntityId());
        pc1.getItemSlots().write(0, EnumWrappers.ItemSlot.LEGS);
        pc1.getItemModifier().write(0, null);

        PacketContainer pc2 = pm.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        pc2.getIntegers().write(0, p.getEntityId());
        pc2.getItemSlots().write(0, EnumWrappers.ItemSlot.CHEST);
        pc2.getItemModifier().write(0, null);

        PacketContainer pc3 = pm.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        pc3.getIntegers().write(0, p.getEntityId());
        pc3.getItemSlots().write(0, EnumWrappers.ItemSlot.HEAD);
        pc3.getItemModifier().write(0, null);

        for (Player players : Bukkit.getOnlinePlayers()) {

            if (players.getUniqueId().equals(p.getUniqueId())) continue;

            try {
                pm.sendServerPacket(players, pc);
                pm.sendServerPacket(players, pc1);
                pm.sendServerPacket(players, pc2);
                pm.sendServerPacket(players, pc3);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void restoreArmor(Player p) {

        ProtocolManager pm = ProtocolLibrary.getProtocolManager();

        PacketContainer pc = pm.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        pc.getIntegers().write(0, p.getEntityId());
        pc.getItemSlots().write(0, EnumWrappers.ItemSlot.FEET);
        pc.getItemModifier().write(0, p.getInventory().getBoots());

        PacketContainer pc1 = pm.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        pc1.getIntegers().write(0, p.getEntityId());
        pc1.getItemSlots().write(0, EnumWrappers.ItemSlot.LEGS);
        pc1.getItemModifier().write(0, p.getInventory().getLeggings());

        PacketContainer pc2 = pm.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        pc2.getIntegers().write(0, p.getEntityId());
        pc2.getItemSlots().write(0, EnumWrappers.ItemSlot.CHEST);
        pc2.getItemModifier().write(0, p.getInventory().getChestplate());

        PacketContainer pc3 = pm.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        pc3.getIntegers().write(0, p.getEntityId());
        pc3.getItemSlots().write(0, EnumWrappers.ItemSlot.HEAD);
        pc3.getItemModifier().write(0, p.getInventory().getHelmet());

        for (Player players : Bukkit.getOnlinePlayers()) {

            if (players.getUniqueId().equals(p.getUniqueId())) continue;

            try {
                pm.sendServerPacket(players, pc);
                pm.sendServerPacket(players, pc1);
                pm.sendServerPacket(players, pc2);
                pm.sendServerPacket(players, pc3);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
