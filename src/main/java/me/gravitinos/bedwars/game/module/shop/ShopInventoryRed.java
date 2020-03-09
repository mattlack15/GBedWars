package me.gravitinos.bedwars.game.module.shop;

import me.gravitinos.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.Material;

public class ShopInventoryRed extends ShopInventory{

    public ShopInventoryRed(ModuleGameItems gameItems){
        super(ShopInventoryUtil.SECTION_BLOCKS);
        this.setSectionDisplayItem(ShopInventoryUtil.SECTION_BLOCKS, new ItemBuilder(Material.DIRT, 1, (byte) 1).setName("&f" + ShopInventoryUtil.SECTION_BLOCKS).addLore("&fBuilding Blocks").build());

        //Team Dependant
        this.addShopItem(new ShopItem(new ItemBuilder(gameItems.getGameItem(BedwarsItem.RESOURCE_IRON.toString()).getItem(1)).setAmount(3).build(), new ItemBuilder(Material.WOOL, 8, (byte) 14).build()));

        //Non-Team dependant
        ShopInventoryUtil.addNonTeamDependantItems(this, gameItems);
    }

}
