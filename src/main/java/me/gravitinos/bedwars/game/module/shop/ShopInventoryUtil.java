package me.gravitinos.bedwars.game.module.shop;

import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.game.info.PermanentArmorType;
import me.gravitinos.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ShopInventoryUtil {
    public static final String SECTION_BLOCKS = "Blocks";
    public static final String SECTION_WEAPONS = "Weapons";
    public static final String SECTION_ARMOUR = "Armour";
    public static final String SECTION_MISC = "Misc";

    public static void addNonTeamDependantItems(@NotNull ShopInventory inventory, @NotNull ModuleGameItems gameItems){
        inventory.addShopItem(ShopInventoryUtil.SECTION_MISC,
                new ShopItem(new ItemBuilder(gameItems.getGameItem(BedwarsItem.RESOURCE_EMERALD.toString()).getItem(1)).setAmount(2).build(),
                        new ItemBuilder(gameItems.getGameItem(BedwarsItem.ITEM_ENDERPEARL.toString()).getItem(1)).setAmount(1).build()));

        inventory.addShopItem(ShopInventoryUtil.SECTION_MISC,
                new ShopItem(getItem(BedwarsItem.RESOURCE_EMERALD, gameItems, 4),
                        new ItemBuilder(gameItems.getGameItem(BedwarsItem.ITEM_SPACE_ENDERPEARL.toString()).getItem(1)).setAmount(1).build()));

        inventory.addShopItem(ShopInventoryUtil.SECTION_WEAPONS,
                new ShopItem(getItem(BedwarsItem.RESOURCE_GOLD, gameItems, 2), new ItemBuilder(Material.IRON_SWORD, 1).build()));

        inventory.addShopItem(ShopInventoryUtil.SECTION_ARMOUR,
                new ShopItem(getItem(BedwarsItem.RESOURCE_GOLD, gameItems, 6), new ItemBuilder(Material.IRON_CHESTPLATE, 1).setName("&cPermanent &fIron Armor").build(), (pl) -> {
                    pl.getInventory().setLeggings(PermanentArmorType.IRON.getLeggings()); //Btw pl is pL not pONE
                    ((BedwarsHandler)gameItems.getGameHandler()).getPlayerInfo(pl.getUniqueId()).setPermanentArmorType(PermanentArmorType.IRON);
                }, true));

        inventory.addShopItem(ShopInventoryUtil.SECTION_ARMOUR,
                new ShopItem(getItem(BedwarsItem.RESOURCE_EMERALD, gameItems, 4), new ItemBuilder(Material.DIAMOND_CHESTPLATE, 1).setName("&cPermanent &bDiamond Armor").build(), (pl) -> {
                    pl.getInventory().setLeggings(PermanentArmorType.DIAMOND.getLeggings()); //Btw pl is pL not pONE
                    ((BedwarsHandler)gameItems.getGameHandler()).getPlayerInfo(pl.getUniqueId()).setPermanentArmorType(PermanentArmorType.DIAMOND);
                }, true));

        inventory.addShopItem(ShopInventoryUtil.SECTION_BLOCKS,
                new ShopItem(getItem(BedwarsItem.RESOURCE_GOLD, gameItems, 3), new ItemBuilder(Material.ENDER_STONE, 4).build()));

    }

    public static ItemStack getItem(BedwarsItem item, ModuleGameItems gameItems, int amount){
        return new ItemBuilder(gameItems.getGameItem(item.toString()).getItem(1)).setAmount(amount).build();
    }

}
