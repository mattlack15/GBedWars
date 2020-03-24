package me.gravitinos.bedwars.game.module.shop;

import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ShopItem {
    private Supplier<ItemStack> needed;
    private Consumer<Player> giver;
    private Supplier<ItemStack> displayItemSupplier;
    private boolean autoPriceAdding;

    public ShopItem(ItemStack needed, @NotNull ItemStack displayItem, @NotNull Consumer<Player> giver, boolean autoPriceAdding){
        this(() -> needed, () -> displayItem, giver, autoPriceAdding);
    }

    public ShopItem(Supplier<ItemStack> needed, @NotNull Supplier<ItemStack> displayItemSupplier, @NotNull Consumer<Player> giver, boolean autoPriceAdding){
        this.needed = needed;
        this.giver = giver;
        this.displayItemSupplier = displayItemSupplier;
        this.autoPriceAdding = autoPriceAdding;
    }

    public ShopItem(ItemStack needed, @NotNull ItemStack displayItem, @NotNull ItemStack item, boolean autoPriceAdding){
        this(needed, displayItem, (p) -> p.getInventory().addItem(item), autoPriceAdding);
    }

    public ShopItem(ItemStack needed, @NotNull ItemStack item){
        this(needed, addPrice(item, needed), (p) -> p.getInventory().addItem(item), false);
    }

    public static ItemStack addPrice(@NotNull ItemStack item, ItemStack needed){
        return new ItemBuilder(item).addLore("&eCost: &b" + (needed != null ? needed.getAmount() + "x " + (needed.hasItemMeta() ? needed.getItemMeta().getDisplayName() : needed.getType().name()) : "&aNothing")).build();
    }

    public Consumer<Player> getGiver() {
        return giver;
    }

    public ItemStack getDisplayItem() {
        return autoPriceAdding ? addPrice(displayItemSupplier.get(), needed.get()) : displayItemSupplier.get();
    }

    @Nullable
    public Supplier<ItemStack> getNeeded() {
        return needed;
    }
}
