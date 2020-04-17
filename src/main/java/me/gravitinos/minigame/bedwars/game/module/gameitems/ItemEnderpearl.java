package me.gravitinos.minigame.bedwars.game.module.gameitems;

import me.gravitinos.minigame.bedwars.game.BedwarsHandler;
import me.gravitinos.minigame.gamecore.CoreHandler;
import me.gravitinos.minigame.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.minigame.gamecore.gameitem.SimpleGameItemHandler;
import me.gravitinos.minigame.gamecore.util.EventSubscription;
import me.gravitinos.minigame.gamecore.util.ItemBuilder;
import me.gravitinos.minigame.gamecore.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemEnderpearl extends SimpleGameItemHandler {

    public static final int COOLDOWN_SECONDS = 5;

    public static final String META_VAL = "BW_EPEARL";

    private Map<UUID, Long> lastUsed = new HashMap<>();

    public ItemEnderpearl(@NotNull ModuleGameItems module) {
        super(module, BedwarsItem.ITEM_ENDERPEARL.toString());
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public String getDescription() {
        return "Teleports you to where you throw it";
    }

    @Override
    public ItemStack getItem(int level) {
        ItemBuilder builder = new ItemBuilder(Material.ENDER_PEARL, 1).setName(ChatColor.LIGHT_PURPLE + "Enderpearl");
        for(String lines :  TextUtil.splitIntoLines(this.getDescription(), 20)){
            builder.addLore(ChatColor.GRAY + lines);
        }
        return builder.build();
    }

    @EventSubscription
    private void onLand(ProjectileHitEvent event){
        if(event.getEntity().hasMetadata(META_VAL)){
            if(!getModule().getGameHandler().isRunning()){
                event.getEntity().setShooter(null);
                event.getEntity().remove();
            }
        }
    }

    @EventSubscription
    private void onInteract(PlayerInteractEvent event){
        if(getModule().getGameHandler().isPlaying(event.getPlayer().getUniqueId())){
            if (event.getPlayer().getEquipment().getItemInMainHand() != null) {
                if (this.isMatch(event.getPlayer().getEquipment().getItemInMainHand())) {
                    if(this.lastUsed.containsKey(event.getPlayer().getUniqueId())){
                        long timeSinceLastUse = System.currentTimeMillis() - this.lastUsed.get(event.getPlayer().getUniqueId());
                        if(timeSinceLastUse < COOLDOWN_SECONDS * 1000 && timeSinceLastUse > 10){
                            event.setCancelled(true);
                            ((BedwarsHandler)getModule().getGameHandler()).sendGameMessage(event.getPlayer(),ChatColor.GRAY + "You must wait 5 seconds before firing another enderpearl!", "Game");
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventSubscription
    private void onThrow(ProjectileLaunchEvent event){
        if(event.getEntity().getShooter() instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) event.getEntity().getShooter();
            if (getModule().getGameHandler().isPlaying(livingEntity.getUniqueId())) {
                if (livingEntity.getEquipment().getItemInMainHand() != null) {
                    if (this.isMatch(livingEntity.getEquipment().getItemInMainHand())) {
                        if(this.lastUsed.containsKey(livingEntity.getUniqueId())){
                            long timeSinceLastUse = System.currentTimeMillis() - this.lastUsed.get(livingEntity.getUniqueId());
                            if(timeSinceLastUse < COOLDOWN_SECONDS * 1000 && timeSinceLastUse > 10){
                                event.setCancelled(true);
                                return;
                            }
                        }
                        this.lastUsed.put(livingEntity.getUniqueId(), System.currentTimeMillis());
                        event.getEntity().setMetadata(META_VAL, new FixedMetadataValue(CoreHandler.main, null));
                    }
                }
            }
        }
    }
}
