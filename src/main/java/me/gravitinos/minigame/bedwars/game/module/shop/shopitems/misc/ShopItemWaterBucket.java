package me.gravitinos.minigame.bedwars.game.module.shop.shopitems.misc;

import me.gravitinos.minigame.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.minigame.bedwars.game.module.shop.EnumShopSection;
import me.gravitinos.minigame.bedwars.game.module.shop.shopitems.SimpleShopItemData;
import me.gravitinos.minigame.gamecore.util.ItemBuilder;
import org.bukkit.Material;

public class ShopItemWaterBucket extends SimpleShopItemData {
    public ShopItemWaterBucket() {
        super(EnumShopSection.SECTION_MISC, new ItemBuilder(Material.WATER_BUCKET, 1).setName("&bHoly Water").build(), BedwarsItem.RESOURCE_IRON, 20);
    }
}
