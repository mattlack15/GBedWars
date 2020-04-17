package me.gravitinos.minigame.bedwars.game.module;

import me.gravitinos.minigame.bedwars.game.BedwarsHandler;
import me.gravitinos.minigame.bedwars.game.module.shop.Shop;
import me.gravitinos.minigame.gamecore.module.GameHandler;
import me.gravitinos.minigame.gamecore.module.GameModule;
import me.gravitinos.minigame.gamecore.util.EventSubscription;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ModuleShops extends GameModule {

    private ArrayList<Shop> shops;

    public ModuleShops(@NotNull ArrayList<Shop> shops) {
        super("SHOPS");
        this.shops = shops;
    }

    public void enable(){
        super.enable();
        Location mid = null;
        if(((BedwarsHandler)getGameHandler()).getPointTracker().getMidGens().size() > 0){
            mid = ((BedwarsHandler)getGameHandler()).getPointTracker().getMidGens().get(0);
        }
        Location finalMid = mid;
        this.shops.forEach(s -> s.createEntity(finalMid));
    }

    public void disable(){
        super.disable();
        this.shops.forEach(Shop::removeEntity);
    }

    @EventSubscription
    private void onClick(PlayerInteractEntityEvent event){
        for(Shop shop : this.shops){
            if(event.getRightClicked().getUniqueId().equals(shop.getEntityUUID()) && shop.getEntityUUID() != null){
                shop.open(event.getPlayer());
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.5f, 1f);
                event.setCancelled(true);
                return;
            }
        }
    }
}
