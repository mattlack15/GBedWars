package me.gravitinos.bedwars.gamecore.util;

import me.gravitinos.bedwars.gamecore.util.Saving.SavedInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public final class TeamDresser {
    private static ItemStack generateItem(Material mat, int color) {
        return new ItemBuilder(mat, 1)
                .setUnbreakable(true)
                .build(); //TODO
    }

    /**
     * Save and clear the inventory of the player
     * then create colored armor
     * @param p Player
     * @param colour Armor color
     * @return Saved Inventory
     */
    public static SavedInventory clearAndDressPlayer(Player p, int colour){
        SavedInventory inventory = new SavedInventory(p.getInventory().getContents(), p.getInventory().getArmorContents());

        p.getInventory().clear();

        ItemStack[] armor = new ItemStack[4];

        armor[0] = generateItem(Material.LEATHER_BOOTS, colour);
        armor[1] = generateItem(Material.LEATHER_LEGGINGS, colour);
        armor[2] = generateItem(Material.LEATHER_CHESTPLATE, colour);
        armor[3] = generateItem(Material.LEATHER_HELMET, colour);
        p.getInventory().setArmorContents(armor);

        return inventory;

    }

    /**
     * Set the color of a leather armor piece
     * @param item Item
     * @param colour Color
     * @return Modified item
     */
    public static ItemStack setLeatherColour(ItemStack item, int colour){
        if(item.getItemMeta() instanceof LeatherArmorMeta){
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(org.bukkit.Color.fromRGB(colour));
            item.setItemMeta(meta);
        }
        return item;
    }
}
