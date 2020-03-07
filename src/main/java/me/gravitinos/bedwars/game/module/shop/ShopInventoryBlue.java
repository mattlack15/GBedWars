package me.gravitinos.bedwars.game.module.shop;

import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.game.SpigotBedwars;
import me.gravitinos.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.bedwars.gamecore.gameitem.GameItemHandler;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.bedwars.gamecore.handler.GameHandler;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ShopInventoryBlue extends ShopInventory{

    public ShopInventoryBlue(ModuleGameItems gameItems){
        this.addShopItem(new ShopItem(new ItemBuilder(gameItems.getGameItem(BedwarsItem.RESOURCE_IRON.toString()).getItem(1)).setAmount(16).build(),
                new ItemBuilder(Material.WOOL, 1, (byte) 5).build()));
    }

    @Override
    public @NotNull String getMainSection() {
        return ShopInventoryUtil.SECTION_BLOCKS;
    }
}
