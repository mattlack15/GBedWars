package me.gravitinos.minigame.bedwars.game.module.shop.shopitems.potions;

import me.gravitinos.minigame.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.minigame.bedwars.game.module.shop.EnumShopSection;
import me.gravitinos.minigame.bedwars.game.module.shop.shopitems.SimpleShopItemData;
import me.gravitinos.minigame.gamecore.util.ItemBuilder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionType;

public class ShopItemHealthPotion extends SimpleShopItemData {
    public ShopItemHealthPotion() {
        super(EnumShopSection.SECTION_POTIONS, new ItemBuilder(PotionType.INSTANT_HEAL, false, false, true).setName("&cKoolaid")
                        .addLore("&7Tastes so good it heals you").addItemFlags(ItemFlag.HIDE_POTION_EFFECTS).build(),
                BedwarsItem.RESOURCE_EMERALD, 1);
    }
}
