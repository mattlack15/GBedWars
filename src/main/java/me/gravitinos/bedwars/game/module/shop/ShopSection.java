package me.gravitinos.bedwars.game.module.shop;

import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ShopSection {
    private String sectionName;
    private ItemStack displayItem = null;
    private ArrayList<ShopItem> shopItems = new ArrayList<>();

    public ShopSection(String name){
        this.sectionName = name;
    }

    public ItemStack getDisplayItem() {
        if(displayItem == null) return new ItemBuilder(Material.EMPTY_MAP, 1).setName("&f" + getSectionName()).build();
        return displayItem;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public void addShopItem(ShopItem item) {
        this.shopItems.add(item);
    }

    public ArrayList<ShopItem> getShopItems() {
        return shopItems;
    }
}
