package me.gravitinos.minigame.bedwars.game.module.shop;

import me.gravitinos.minigame.gamecore.gameitem.ModuleGameItems;

public class ShopInventoryDefault extends ShopInventory{

    public ShopInventoryDefault(ModuleGameItems gameItems){
        super(EnumShopSection.SECTION_BLOCKS.getName());
    }

}
