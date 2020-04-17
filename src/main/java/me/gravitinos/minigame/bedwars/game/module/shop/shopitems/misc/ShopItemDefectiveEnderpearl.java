package me.gravitinos.minigame.bedwars.game.module.shop.shopitems.misc;

import me.gravitinos.minigame.bedwars.game.info.BWPlayerInfo;
import me.gravitinos.minigame.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.minigame.bedwars.game.module.shop.EnumShopSection;
import me.gravitinos.minigame.bedwars.game.module.shop.ShopItemData;
import me.gravitinos.minigame.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.minigame.gamecore.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ShopItemDefectiveEnderpearl extends ShopItemData {
    @Override
    public @NotNull Consumer<Player> getGiver(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return (p) -> p.getInventory().addItem(gameItems.getGameItem(BedwarsItem.ITEM_DEFECTIVE_ENDERPEARL.toString()).getItem(1));
    }

    @Override
    public @NotNull Supplier<ItemStack> getDisplayItemSupplier(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return () -> gameItems.getGameItem(BedwarsItem.ITEM_DEFECTIVE_ENDERPEARL.toString()).getItem(1);
    }

    @Override
    public @NotNull String getSection() {
        return EnumShopSection.SECTION_MISC.getName();
    }

    @Override
    public @NotNull ItemStack getCost(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return new ItemBuilder(gameItems.getGameItem(BedwarsItem.RESOURCE_EMERALD.toString()).getItem(1)).setAmount(4).build();
    }

    @Override
    public boolean isItemCostIncluded() {
        return false;
    }
}
