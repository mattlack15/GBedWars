package me.gravitinos.bedwars.game.module;

import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.game.module.shop.Shop;
import me.gravitinos.bedwars.gamecore.handler.GameHandler;
import me.gravitinos.bedwars.gamecore.module.GameModule;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ModuleShops extends GameModule {

    private ArrayList<Shop> shops;

    public ModuleShops(@NotNull GameHandler gameHandler, @NotNull ArrayList<Shop> shops) {
        super(gameHandler, "SHOPS");
        this.shops = shops;

        Location mid = null;
        if(((BedwarsHandler)gameHandler).getPointTracker().getMidGens().size() > 0){
            mid = ((BedwarsHandler)gameHandler).getPointTracker().getMidGens().get(0);
        }
        Location finalMid = mid;
        this.shops.forEach(s -> s.createEntity(finalMid));
    }

    public void cleanup(){
        this.shops.forEach(Shop::removeEntity);
    }

    @EventSubscription
    private void onClick(PlayerInteractEntityEvent event){
        for(Shop shop : this.shops){
            if(event.getRightClicked().getUniqueId().equals(shop.getEntityUUID()) && shop.getEntityUUID() != null){
                shop.open(event.getPlayer());
                event.setCancelled(true);
                return;
            }
        }
    }
}
