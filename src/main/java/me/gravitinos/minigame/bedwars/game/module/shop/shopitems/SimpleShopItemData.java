package me.gravitinos.minigame.bedwars.game.module.shop.shopitems;

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

public class SimpleShopItemData extends ShopItemData {
    private BedwarsItem COST_RESOURCE;
    private int COST_AMOUNT;

    private ItemStack item;

    private EnumShopSection section;

    public SimpleShopItemData(EnumShopSection section, ItemStack item, BedwarsItem costResource, int costAmount){
        this.COST_AMOUNT = costAmount;
        this.COST_RESOURCE = costResource;
        this.item = item;
        this.section = section;
    }

    @Override
    public @NotNull Consumer<Player> getGiver(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return (player -> player.getInventory().addItem(item));
    }

    @Override
    public @NotNull Supplier<ItemStack> getDisplayItemSupplier(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return () -> item;
    }

    @Override
    public @NotNull String getSection() {
        return section.getName();
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
