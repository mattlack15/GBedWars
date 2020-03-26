package me.gravitinos.bedwars.game.module.shop.shopitems.misc;

import me.gravitinos.bedwars.game.info.BWPlayerInfo;
import me.gravitinos.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.bedwars.game.module.shop.EnumShopSection;
import me.gravitinos.bedwars.game.module.shop.ShopItemData;
import me.gravitinos.bedwars.game.module.shop.shopitems.SimpleShopItemData;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ShopItemBridgeEgg extends ShopItemData {
    @Override
    public @NotNull Consumer<Player> getGiver(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return (p) -> p.getInventory().addItem(new ItemBuilder(gameItems.getGameItem(BedwarsItem.ITEM_BRIDGE_EGG.toString()).getItem(1)).build());
    }

    @Override
    public @NotNull Supplier<ItemStack> getDisplayItemSupplier(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return () -> gameItems.getGameItem(BedwarsItem.ITEM_BRIDGE_EGG.toString()).getItem(1);
    }

    @Override
    public @NotNull String getSection() {
        return EnumShopSection.SECTION_MISC.getName();
    }

    @Override
    public @NotNull ItemStack getCost(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return new ItemBuilder(gameItems.getGameItem(BedwarsItem.RESOURCE_GOLD.toString()).getItem(1)).setAmount(8).build();
    }

    @Override
    public boolean isItemCostIncluded() {
        return false;
    }
}
