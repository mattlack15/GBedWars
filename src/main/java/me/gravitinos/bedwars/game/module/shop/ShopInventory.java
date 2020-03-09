package me.gravitinos.bedwars.game.module.shop;

import com.google.common.collect.Lists;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class ShopInventory {
    private Map<String, ArrayList<ShopItem>> inventory = new HashMap<>();

    private Map<String, ItemStack> sectionDisplayItems = new HashMap<>();

    private String mainSection;

    public ShopInventory(@NotNull String mainSection){
        this.mainSection = mainSection;
    }

    public void addShopItem(@NotNull String section, @NotNull ShopItem item){
        if(!inventory.containsKey(section)){
            inventory.put(section, new ArrayList<>());
        }
        ArrayList<ShopItem> items = inventory.get(section);
        items.add(item);
    }

    public void setSectionDisplayItem(@NotNull String section, @NotNull ItemStack stack){
        sectionDisplayItems.put(section, stack);
    }

    public ItemStack getSectionDisplayItem(@NotNull String section){
        ItemStack stack = sectionDisplayItems.get(section);
        if(stack == null){
            return new ItemBuilder(Material.EMPTY_MAP, 1).setName("&f" + section).build();
        }
        return stack;
    }

    public void addShopItem(@NotNull ShopItem item){
        this.addShopItem(getMainSection(), item);
    }

    @NotNull
    public String getMainSection(){
        return this.mainSection;
    }

    public void setMainSection(@NotNull String mainSection) {
        this.mainSection = mainSection;
    }

    /**
     * Get the valid sections in this shop inventory
     * @return The valid sections
     */
    public ArrayList<String> getSections(){
        return Lists.newArrayList(inventory.keySet());
    }

    /**
     * Get the items from a section
     * @param section The section to get from
     * @return The shop items in the specified section
     */
    public ArrayList<ShopItem> getItems(String section){
        ArrayList<ShopItem> items = inventory.get(section);
        return items != null ? items : new ArrayList<>();
    }
}
