package me.gravitinos.bedwars.game.module;

import me.gravitinos.bedwars.game.module.shop.Shop;
import me.gravitinos.bedwars.gamecore.handler.GameHandler;
import me.gravitinos.bedwars.gamecore.module.GameModule;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ModuleShops extends GameModule {

    private ArrayList<Shop> shops;

    public ModuleShops(@NotNull GameHandler gameHandler, @NotNull ArrayList<Shop> shops) {
        super(gameHandler, "SHOPS");
        this.shops = shops;
        this.shops.forEach(Shop::createEntity);
    }

    @EventSubscription
    private void onClick(PlayerInteractEntityEvent event){
        for(Shop shop : this.shops){
            if(event.getRightClicked().getUniqueId().equals(shop.getEntityID()) && shop.getEntityID() != null){

            }
        }
    }
}
