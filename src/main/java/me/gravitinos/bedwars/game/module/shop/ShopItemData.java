package me.gravitinos.bedwars.game.module.shop;

import me.gravitinos.bedwars.game.info.BWPlayerInfo;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ShopItemData {
    /**
     * Get the giver, customized to the specified player
     * @param playerInfo The info of the player it is being made for
     * @return Giver
     */
    @NotNull
    public abstract Consumer<Player> getGiver(BWPlayerInfo playerInfo, ModuleGameItems gameItems);

    @NotNull
    public abstract Supplier<ItemStack> getDisplayItemSupplier(BWPlayerInfo playerInfo, ModuleGameItems gameItems);

    @NotNull
    public abstract String getSection();

    @NotNull
    public abstract ItemStack getCost(BWPlayerInfo playerInfo, ModuleGameItems gameItems);

    /**
     * Get if the item cost is already included in the display item supplier
     */
    public abstract boolean isItemCostIncluded();
}
