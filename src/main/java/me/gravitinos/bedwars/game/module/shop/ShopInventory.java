package me.gravitinos.bedwars.game.module.shop;

import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class ShopInventory {
    private ArrayList<ShopSection> sections = new ArrayList<>();

    private Map<String, ItemStack> sectionDisplayItems = new HashMap<>();

    private String mainSection;

    public ShopInventory(@NotNull String mainSection) {
        this.mainSection = mainSection;
    }

    public void addShopItem(@NotNull String section, @NotNull ShopItem item) {
        ShopSection sect = getSection(section);
        if (sect != null) {
            sect.addShopItem(item);
            return;
        }
        sect = new ShopSection(section);
        sect.addShopItem(item);
        this.sections.add(sect);
    }

    public void setSectionDisplayItem(@NotNull String section, @NotNull ItemStack stack) {
        ShopSection sect = getSection(section);
        if(sect == null){
            sect = new ShopSection(section);
            this.addSection(sect);
        }
        sect.setDisplayItem(stack);
    }

    public ItemStack getSectionDisplayItem(@NotNull String section) {
        ShopSection sect = getSection(section);
        return sect.getDisplayItem();
    }

    public void addSection(ShopSection section) {
        if (!sections.contains(section)) {
            this.sections.add(section);
        }
    }

    public void addShopItem(@NotNull ShopItem item) {
        this.addShopItem(getMainSection(), item);
    }

    @NotNull
    public String getMainSection() {
        return this.mainSection;
    }

    public void setMainSection(@NotNull String mainSection) {
        this.mainSection = mainSection;
    }

    /**
     * Get the valid sections in this shop inventory
     *
     * @return The valid sections
     */
    public ArrayList<ShopSection> getSections() {
        return this.sections;
    }

    /**
     * Get the items from a section
     *
     * @param section The section to get from
     * @return The shop items in the specified section
     */
    public ArrayList<ShopItem> getItems(String sectionName) {
        ShopSection section = getSection(sectionName);
        if (section == null) {
            return new ArrayList<>();
        }
        return section.getShopItems();
    }

    public ShopSection getSection(@NotNull String sectionName) {
        for (ShopSection sections : this.sections) {
            if (sections.getSectionName().equals(sectionName)) {
                return sections;
            }
        }
        return null;
    }
}
