package me.gravitinos.bedwars.game.module.shop.shopitems.misc;

import me.gravitinos.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.bedwars.game.module.shop.EnumShopSection;
import me.gravitinos.bedwars.game.module.shop.shopitems.SimpleShopItemData;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

public class ShopItemHealthPotion extends SimpleShopItemData {
    public ShopItemHealthPotion() {
        super(EnumShopSection.SECTION_MISC, new ItemBuilder(PotionType.INSTANT_HEAL, false, false, true).setName("&cKoolaid").addItemFlags(ItemFlag.HIDE_POTION_EFFECTS).build(),
                BedwarsItem.RESOURCE_EMERALD, 1);
    }
}
