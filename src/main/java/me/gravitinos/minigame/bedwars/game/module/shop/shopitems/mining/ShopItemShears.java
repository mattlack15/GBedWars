package me.gravitinos.minigame.bedwars.game.module.shop.shopitems.mining;

import me.gravitinos.minigame.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.minigame.bedwars.game.module.shop.EnumShopSection;
import me.gravitinos.minigame.bedwars.game.module.shop.shopitems.SimpleShopItemData;
import me.gravitinos.minigame.gamecore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

public class ShopItemShears extends SimpleShopItemData {
    public ShopItemShears() {
        super(EnumShopSection.SECTION_MINING, new ItemBuilder(Material.SHEARS, 1).setName("&7Scissors").addItemFlags(ItemFlag.HIDE_ATTRIBUTES).build(), BedwarsItem.RESOURCE_GOLD, 4);
    }
}
