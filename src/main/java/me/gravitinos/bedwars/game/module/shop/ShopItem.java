package me.gravitinos.bedwars.game.module.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ShopItem {
    private ItemStack needed;
    private Consumer<Player> giver;
    private ItemStack displayItem;

    public ShopItem(ItemStack needed, @NotNull ItemStack displayItem, @NotNull Consumer<Player> giver){
        this.needed = needed;
        this.giver = giver;
        this.displayItem = displayItem;
    }

    public ShopItem(ItemStack needed, @NotNull ItemStack displayItem, @NotNull ItemStack item){
        this(needed, displayItem, (p) -> p.getInventory().addItem(item));
    }

    public Consumer<Player> getGiver() {
        return giver;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    @Nullable
    public ItemStack getNeeded() {
        return needed;
    }
}
