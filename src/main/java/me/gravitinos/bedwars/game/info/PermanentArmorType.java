package me.gravitinos.bedwars.game.info;

import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum PermanentArmorType {
    LEATHER("&e&lLeather", Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET),
    IRON("&f&lIron", Material.IRON_BOOTS, Material.IRON_LEGGINGS, Material.IRON_CHESTPLATE, Material.IRON_HELMET),
    DIAMOND("&b&lDiamond", Material.DIAMOND_BOOTS, Material.DIAMOND_LEGGINGS, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_HELMET);

    private Material boots;
    private Material leggings;
    private Material chestplate;
    private Material helmet;
    private String displayName;

    PermanentArmorType(String displayName, Material boots, Material leggings, Material chestplate, Material helmet){
        this.boots = boots;
        this.leggings = leggings;
        this.chestplate = chestplate;
        this.helmet = helmet;
        this.displayName = ChatColor.translateAlternateColorCodes('&', displayName);
    }

    public String getDisplayName() {
        return displayName;
    }

    public ItemStack getBoots() {
        return new ItemBuilder(boots, 1).setUnbreakable(true).build();
    }

    public ItemStack getLeggings() {
        return new ItemBuilder(leggings, 1).setUnbreakable(true).build();
    }

    public ItemStack getChestplate() {
        return new ItemBuilder(chestplate, 1).setUnbreakable(true).build();
    }

    public ItemStack getHelmet() {
        return new ItemBuilder(helmet, 1).setUnbreakable(true).build();
    }
}
