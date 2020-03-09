package me.gravitinos.bedwars.game.module.gameitems;

import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.bedwars.gamecore.gameitem.SimpleGameItemHandler;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import me.gravitinos.bedwars.gamecore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemSpaceEnderpearl extends SimpleGameItemHandler {

    public static final int COOLDOWN_SECONDS = 5;

    public static final String META_VAL = "BW_EPEARL";

    private Map<UUID, Long> lastUsed = new HashMap<>();

    public ItemSpaceEnderpearl(@NotNull ModuleGameItems module) {
        super(module, BedwarsItem.ITEM_SPACE_ENDERPEARL.toString());
    }

    @Override
    public String getDescription() {
        return "Teleports you to where you throw it, this enderpearl has &cno gravity";
    }

    @Override
    public ItemStack getItem(int level) {
        ItemBuilder builder = new ItemBuilder(Material.ENDER_PEARL, 1).setName(ChatColor.DARK_RED + "Space Enderpearl");
        for(String lines :  TextUtil.splitIntoLines(this.getDescription(), 20)){
            builder.addLore(ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', lines));
        }
        builder.addEnchantment(Enchantment.DIG_SPEED, 1);
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
                                ((BedwarsHandler)getModule().getGameHandler()).sendGameMessage(livingEntity,ChatColor.GRAY + "You must wait 5 seconds before firing another space enderpearl!", "Game");
                                return;
                            }
                        }
                        this.lastUsed.put(livingEntity.getUniqueId(), System.currentTimeMillis());
                        event.getEntity().setGravity(false);
                        event.getEntity().setMetadata(META_VAL, new FixedMetadataValue(CoreHandler.main, null));
                    }
                }
            }
        }
    }
}
