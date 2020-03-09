package me.gravitinos.bedwars.game.module;

import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.gamecore.handler.GameHandler;
import me.gravitinos.bedwars.gamecore.module.GameModule;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class ModuleDamageHandler extends GameModule {
    public ModuleDamageHandler(@NotNull GameHandler gameHandler) {
        super(gameHandler, "DAMAGE_HANDLER");
    }

    @EventSubscription
    private void onDamage(EntityDamageEvent event){
        if(getGameHandler().isSpectating(event.getEntity().getUniqueId()) || ((BedwarsHandler)getGameHandler()).isRespawning(event.getEntity().getUniqueId())){
            event.setCancelled(true);
            return;
        }
        if(getGameHandler().isPlaying(event.getEntity().getUniqueId())){

            String cause = event.getCause().name().toLowerCase();
            String with = null;
            Player by = null;

            if(event instanceof EntityDamageByEntityEvent){
                EntityDamageByEntityEvent e1 = (EntityDamageByEntityEvent) event;
                if(!getGameHandler().isPlaying(e1.getDamager().getUniqueId()) || ((BedwarsHandler)getGameHandler()).isRespawning(e1.getDamager().getUniqueId())){
                    event.setCancelled(true);
                    return;
                }
                cause = e1.getDamager().getName(); //Set cause (by) to damager's name
                if(e1.getDamager() instanceof LivingEntity){
                    LivingEntity livingEntity = (LivingEntity) e1.getDamager();
                    if(livingEntity.getEquipment().getItemInMainHand().hasItemMeta()) {
                        with = livingEntity.getEquipment().getItemInMainHand().getItemMeta().getDisplayName(); //Set with to the name of the entity's item in main hand
                    }
                }
                if(e1.getDamager() instanceof Player){
                    by = (Player)e1.getDamager();
                }
            }

            if(!(event.getEntity() instanceof Player)){
                return;
            }

            Player player = (Player)event.getEntity();
            if(player.getHealth() - event.getFinalDamage() <= 0){
                //Player died
                event.setCancelled(true);
                ((BedwarsHandler)getGameHandler()).killPlayer(player.getUniqueId(), cause + (with != null ? " &7with&e " + with : ""));
                if(by != null){
                    by.getWorld().playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 5f, 1f);
                }
            }
        }
    }
}
