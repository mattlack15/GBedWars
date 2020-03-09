package me.gravitinos.bedwars.game.module.shop;

import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ShopItem {
    private ItemStack needed;
    private Consumer<Player> giver;
    private ItemStack displayItem;

    public ShopItem(ItemStack needed, @NotNull ItemStack displayItem, @NotNull Consumer<Player> giver, boolean autoPriceAdding){
        this.needed = needed;
        this.giver = giver;
        this.displayItem = displayItem.clone();
        if(autoPriceAdding){
            this.displayItem = addPrice(this.displayItem, needed);
        }
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
        return displayItem;
    }

    @Nullable
    public ItemStack getNeeded() {
        return needed;
    }
}
