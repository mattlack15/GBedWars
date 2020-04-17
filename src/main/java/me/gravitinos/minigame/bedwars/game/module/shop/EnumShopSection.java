package me.gravitinos.minigame.bedwars.game.module.shop;

import me.gravitinos.minigame.gamecore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

public enum EnumShopSection {
    SECTION_BLOCKS("Blocks", new ItemBuilder(Material.DIRT, 1, (byte) 1).setName("&7&lBlocks").addItemFlags(ItemFlag.HIDE_ATTRIBUTES).build()),
    SECTION_WEAPONS("Weapons", new ItemBuilder(Material.DIAMOND_SWORD, 1).setName("&c&lWeapons").addItemFlags(ItemFlag.HIDE_ATTRIBUTES).build()),
    SECTION_ARMOUR("Armour", new ItemBuilder(Material.DIAMOND_CHESTPLATE, 1).setName("&b&lArmour").addItemFlags(ItemFlag.HIDE_ATTRIBUTES).build()),
    SECTION_MISC("Misc", new ItemBuilder(Material.WATCH, 1).setName("&e&lMisc").addItemFlags(ItemFlag.HIDE_ATTRIBUTES).build()),
    SECTION_MINING("Mining", new ItemBuilder(Material.DIAMOND_PICKAXE, 1).setName("&b&lMining").addItemFlags(ItemFlag.HIDE_ATTRIBUTES).build()),
    SECTION_POTIONS("Potions", new ItemBuilder(PotionType.INSTANT_HEAL, false, false).addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES).setName("&3&lPotions").build()),
    SECTION_TEAM_UPGRADES("Team Upgrades", new ItemBuilder(Material.BEACON, 1).setName("&d&lTeam Upgrades").addItemFlags(ItemFlag.HIDE_ATTRIBUTES).build());


    private String name;
    private ItemStack displayItem;

    EnumShopSection(String name, ItemStack displayItem){
        this.name = name;
        this.displayItem = displayItem;
    }

    @Override
    public String toString(){
        return this.getName();
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public String getName() {
        return name;
    }
}
