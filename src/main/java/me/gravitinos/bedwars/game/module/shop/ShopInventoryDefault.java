package me.gravitinos.bedwars.game.module.shop;

import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.game.BedwarsTeam;
import me.gravitinos.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.Material;

public class ShopInventoryDefault extends ShopInventory{

    public ShopInventoryDefault(ModuleGameItems gameItems){
        super(EnumShopSection.SECTION_BLOCKS.getName());
    }

}
