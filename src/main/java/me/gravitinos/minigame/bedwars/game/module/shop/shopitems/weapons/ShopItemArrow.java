package me.gravitinos.minigame.bedwars.game.module.shop.shopitems.weapons;

import me.gravitinos.minigame.bedwars.game.info.BWPlayerInfo;
import me.gravitinos.minigame.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.minigame.bedwars.game.module.shop.EnumShopSection;
import me.gravitinos.minigame.bedwars.game.module.shop.ShopItemData;
import me.gravitinos.minigame.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.minigame.gamecore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ShopItemArrow extends ShopItemData {
    @Override
    public @NotNull Consumer<Player> getGiver(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return (p) -> p.getInventory().addItem(new ItemBuilder(Material.ARROW, 3).setUnbreakable(true).build());
    }

    @Override
    public @NotNull Supplier<ItemStack> getDisplayItemSupplier(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return () -> new ItemBuilder(Material.ARROW, 3).build();
    }

    @Override
    public @NotNull String getSection() {
        return EnumShopSection.SECTION_WEAPONS.getName();
    }

    @Override
    public @NotNull ItemStack getCost(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return new ItemBuilder(gameItems.getGameItem(BedwarsItem.RESOURCE_GOLD.toString()).getItem(1)).setAmount(2).build();
    }

    @Override
    public boolean isItemCostIncluded() {
        return false;
    }
}
