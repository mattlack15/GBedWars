package me.gravitinos.bedwars.game.module.shop.shopitems.mining;

import me.gravitinos.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.bedwars.game.module.shop.EnumShopSection;
import me.gravitinos.bedwars.game.module.shop.shopitems.SimpleShopItemData;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopItemShears extends SimpleShopItemData {
    public ShopItemShears() {
        super(EnumShopSection.SECTION_MINING, new ItemBuilder(Material.SHEARS, 1).setName("&7Scissors").build(), BedwarsItem.RESOURCE_GOLD, 4);
    }
}
