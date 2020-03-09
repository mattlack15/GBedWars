package me.gravitinos.bedwars.game.module.shop;

import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.game.BedwarsTeam;
import me.gravitinos.bedwars.gamecore.gameitem.GameItemHandler;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerDependantShop extends Shop {
    private ModuleGameItems gameItems;
    private BedwarsHandler bedwarsHandler;

    public PlayerDependantShop(@NotNull BedwarsHandler bedwarsHandler, @NotNull String displayName, @NotNull Location location, @NotNull ModuleGameItems gameItems) {
        super(displayName, location, new ShopInventoryRed(gameItems));
        this.gameItems = gameItems;
        this.bedwarsHandler = bedwarsHandler;
    }

    @Override
    public void open(Player p){
        BedwarsTeam team = BedwarsTeam.getTeam(bedwarsHandler.getTeamManagerModule().getTeam(p.getUniqueId()));
        if(team == null){
            return;
        }

        switch (team){
            case RED:
                this.setInventory(new ShopInventoryRed(gameItems));
                break;
            case GREEN:
                this.setInventory(new ShopInventoryGreen(gameItems));
                break;
            case YELLOW:
                this.setInventory(new ShopInventoryYellow(gameItems));
                break;
            case BLUE:
                this.setInventory(new ShopInventoryBlue(gameItems));
                break;
        }

        new ShopMenu(this).open(p);
    }
}
