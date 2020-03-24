package me.gravitinos.bedwars.game.module.shop;

import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.game.BedwarsTeam;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerDependantShop extends Shop {
    private ModuleGameItems gameItems;
    private BedwarsHandler bedwarsHandler;

    public PlayerDependantShop(@NotNull BedwarsHandler bedwarsHandler, @NotNull String displayName, @NotNull Location location, @NotNull ModuleGameItems gameItems) {
        super(displayName, location, new ShopInventoryDefault(gameItems));
        this.gameItems = gameItems;
        this.bedwarsHandler = bedwarsHandler;
    }

    @Override
    public void open(Player p){
        BedwarsTeam team = BedwarsTeam.getTeam(bedwarsHandler.getTeamManagerModule().getTeam(p.getUniqueId()));
        if(team == null){
            return;
        }

        this.setInventory(new PlayerDependantShopInventory(bedwarsHandler.getPlayerInfo(p.getUniqueId()), bedwarsHandler.getGameItemsModule()));

        new ShopMenu(this).open(p);
    }
}
