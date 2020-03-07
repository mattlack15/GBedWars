package me.gravitinos.bedwars.game.module.shop;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class ShopInventory {
    private Map<String, ArrayList<ShopItem>> inventory = new HashMap<>();

    public void addShopItem(@NotNull String section, @NotNull ShopItem item){
        if(!inventory.containsKey(section)){
            inventory.put(section, new ArrayList<>());
        }
        ArrayList<ShopItem> items = inventory.get(section);
        items.add(item);
    }

    public void addShopItem(@NotNull ShopItem item){
        this.addShopItem(getMainSection(), item);
    }

    @NotNull
    public abstract String getMainSection();

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
