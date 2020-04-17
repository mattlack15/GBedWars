package me.gravitinos.minigame.bedwars.game.info;

import me.gravitinos.minigame.gamecore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public enum PermanentPickaxeType {
    NONE("&fNone", Material.AIR),
    WOOD("&e&lWood", Material.WOOD_PICKAXE),
    STONE("&7&lStone", Material.STONE_PICKAXE),
    IRON("&f&lIron", Material.IRON_PICKAXE),
    DIAMOND("&b&lDiamond", Material.DIAMOND_PICKAXE);

    private Material mat;
    private String displayName;
    PermanentPickaxeType(String displayName, Material mat){
        this.mat = mat;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getMaterial() {
        return mat;
    }

    public ItemStack getItem(){
        return new ItemBuilder(getMaterial(), 1).setUnbreakable(true).addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE).build();
    }
}
