package me.gravitinos.bedwars.game.module.shop.shopitems.misc;

import me.gravitinos.bedwars.game.info.BWPlayerInfo;
import me.gravitinos.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.bedwars.game.module.shop.EnumShopSection;
import me.gravitinos.bedwars.game.module.shop.ShopItemData;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ShopItemInvisPot extends ShopItemData {

    private static final BedwarsItem COST_RESOURCE = BedwarsItem.RESOURCE_EMERALD;
    private static final int COST_AMOUNT = 2;

    private static final int GIVE_AMOUNT = 1;
    private static final Material GIVE_MATERIAL = Material.POTION;

    @Override
    public @NotNull Consumer<Player> getGiver(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return (player -> player.getInventory().addItem(gameItems.getGameItem(BedwarsItem.ITEM_INVIS_POT.toString()).getItem(1)));
    }

    @Override
    public @NotNull Supplier<ItemStack> getDisplayItemSupplier(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return () -> gameItems.getGameItem(BedwarsItem.ITEM_INVIS_POT.toString()).getItem(1);
    }

    @Override
    public @NotNull String getSection() {
        return EnumShopSection.SECTION_MISC.getName();
    }

    @Override
    public @NotNull ItemStack getCost(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return new ItemBuilder(gameItems.getGameItem(COST_RESOURCE.toString()).getItem(1)).setAmount(COST_AMOUNT).build();
    }

    @Override
    public boolean isItemCostIncluded() {
        return false;
    }
}