package me.gravitinos.minigame.bedwars.game.module.shop.shopitems.blocks;

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

public class ShopItemObsidian extends ShopItemData {

    private static final BedwarsItem COST_RESOURCE = BedwarsItem.RESOURCE_EMERALD;
    private static final int COST_AMOUNT = 2;

    private static final int GIVE_AMOUNT = 1;

    private static final Material GIVE_MATERIAL = Material.OBSIDIAN;

    @Override
    public @NotNull Consumer<Player> getGiver(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return (player -> player.getInventory().addItem(new ItemStack(GIVE_MATERIAL, GIVE_AMOUNT)));
    }

    @Override
    public @NotNull Supplier<ItemStack> getDisplayItemSupplier(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return () -> new ItemBuilder(GIVE_MATERIAL, GIVE_AMOUNT).build();
    }

    @Override
    public @NotNull String getSection() {
        return EnumShopSection.SECTION_BLOCKS.getName();
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
