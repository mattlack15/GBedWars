/*
 * Copyright (c) 2019. UltraDev
 */

package me.gravitinos.gamecore.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemFactory {
    // ItemStack
    private Material mat;
    private int amount;
    private short damage;

    // ItemMeta
    private String name;
    private List<String> lore;
    private Map<Enchantment, Integer> enchants;
    private boolean unbreakable;

    private boolean flagsHidden = true;

    // Skull Info
    private String skullOwner;

    // Leather armor
    private int leatherColor = -1;

    public ItemFactory(@NotNull ItemStack item) {
        this.mat = item.getType();
        this.amount = item.getAmount();
        this.damage = item.getDurability();
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            this.name = meta.hasDisplayName() ? meta.getDisplayName() : null;
            this.lore = meta.hasLore() ? meta.getLore() : null;
            this.enchants = meta.hasEnchants() ? meta.getEnchants() : null;
        }
    }

    /**
     * Create a blank ItemFactory (1 stone)
     */
    public ItemFactory() {
        this(Material.STONE);
    }

    /**
     * Create a default ItemFactory for a specific material
     * @param mat Material
     */
    public ItemFactory(Material mat) {
        this(mat, 1);
    }

    /**
     * Create a default ItemFactory for a specific material
     * with a specific amount
     * @param mat Material
     * @param amount Amount
     */
    public ItemFactory(Material mat, int amount) {
        this(mat, amount, 0);
    }

    /**
     * Create an ItemFactory for a specific material
     * with a specific amount and data
     * @param mat Material
     * @param amount Amount
     * @param damage Data
     */
    public ItemFactory(Material mat, int amount, int damage) {
        this(mat, amount, (short) damage);
    }

    /**
     * Create an ItemFactory for a specific material
     * with a specific amount and data
     * @param mat Material
     * @param amount Amount
     * @param damage Data
     */
    public ItemFactory(Material mat, int amount, short damage) {
        this.mat = mat;
        this.amount = amount;
        this.damage = damage;
        this.lore = new ArrayList<>();
        this.enchants = new HashMap<>();
    }

    /**
     * Set the Material of this ItemFactory
     * @param mat Material
     * @return This
     */
    public ItemFactory setMaterial(Material mat) {
        this.mat = mat;
        return this;
    }

    /**
     * Set the amount of this ItemFactory
     * @param amount Amount
     * @return This
     */
    public ItemFactory setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Set the display name of this ItemFactory
     * @param name Display name
     * @return This
     */
    public ItemFactory setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set the lore of this ItemFactory
     * @param lore Lore
     * @return This
     */
    public ItemFactory setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }

    /**
     * Set the lore of this ItemFactory
     * @param lore Lore
     * @return This
     */
    public ItemFactory setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    /**
     * Add an enchantment to this ItemFactory
     * @param enchant Enchantment
     * @return This
     */
    public ItemFactory addEnchantment(Enchantment enchant) {
        return addEnchantment(enchant, 1);
    }

    /**
     * Add an enchantment to this ItemFactory
     * @param enchant Enchantment
     * @param level Level
     * @return This
     */
    public ItemFactory addEnchantment(Enchantment enchant, int level) {
        if (enchant != null) {
            this.enchants.put(enchant, level);
        }
        return this;
    }

    /**
     * Set the enchantments on this ItemFactory
     * @param ench Enchantments
     * @return This
     */
    public ItemFactory setEnchantments(Map<Enchantment, Integer> ench) {
        this.enchants = ench;
        return this;
    }

    /**
     * Make this item unbreakable (or not unbreakable)
     * @param unbreakable Unbreakable?
     * @return This
     */
    public ItemFactory setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    /**
     * Hide all flags on this item
     * @return This
     */
    public ItemFactory hideFlags() {
        this.flagsHidden = true;
        return this;
    }

    /**
     * Show all flags on this item
     * @return This
     */
    public ItemFactory showFlags() {
        this.flagsHidden = false;
        return this;
    }

    /**
     * Set the skull owner of this item
     * Only works for skulls
     * @param id UUID of skull owner
     * @return This
     */
    public ItemFactory setSkullOwner(UUID id) {
        return setSkullOwner(Bukkit.getOfflinePlayer(id).getName());
    }

    /**
     * Set the skull owner of this item
     * Only works for skulls
     * @param owner Name of skull owner
     * @return This
     */
    public ItemFactory setSkullOwner(String owner) {
        this.skullOwner = owner;
        return this;
    }

    /**
     * Set the leather color of this item
     * Only works for leather armor
     * @param color Color
     * @return This
     */
    public ItemFactory setLeatherColor(int color){
        this.leatherColor = color;
        return this;
    }

    /**
     * Create this item
     * @return Item
     */
    public ItemStack create() {
        ItemStack item = new ItemStack(mat, amount, damage);
        item.addUnsafeEnchantments(enchants);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.spigot().setUnbreakable(unbreakable);
        if (this.flagsHidden) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        item.setItemMeta(meta);
        if (meta instanceof LeatherArmorMeta && leatherColor != -1) {
            LeatherArmorMeta lm = (LeatherArmorMeta) meta;
            lm.setColor(org.bukkit.Color.fromRGB(leatherColor));
            item.setItemMeta(lm);
        }
        if (item.getType().equals(Material.SKULL_ITEM)) {
            SkullMeta sm = (SkullMeta) item.getItemMeta();
            sm.setOwner(this.skullOwner);
            item.setItemMeta(sm);
        }
        return item;
    }
}
