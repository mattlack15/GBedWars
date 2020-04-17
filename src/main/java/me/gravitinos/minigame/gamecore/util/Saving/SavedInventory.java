package me.gravitinos.minigame.gamecore.util.Saving;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SavedInventory {
    private ItemStack[] armorContents;
    private ItemStack[] inventoryContents;

    public SavedInventory(ItemStack[] inventoryContents, ItemStack[] armorContents){
        this.armorContents = armorContents;
        this.inventoryContents = inventoryContents;
    }
    public ItemStack[] getArmorContents(){
        return this.armorContents;
    }
    public ItemStack[] getInventoryContents(){
        return this.inventoryContents;
    }

    /**
     * Restores this inventory to the given player and returns their current inventory
     * @param p The player to restore to
     * @return The player's current inventory
     */
    public SavedInventory restore(@NotNull Player p){
        SavedInventory inv = new SavedInventory(p.getInventory().getContents(), p.getInventory().getArmorContents());
        p.getInventory().setContents(this.inventoryContents);
        p.getInventory().setArmorContents(armorContents);
        return inv;
    }
}
