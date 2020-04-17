package me.gravitinos.minigame.bedwars.game.module.gameitems;

import me.gravitinos.minigame.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.minigame.gamecore.gameitem.SimpleGameItemHandler;
import me.gravitinos.minigame.gamecore.util.ItemBuilder;
import me.gravitinos.minigame.gamecore.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemResourceGold extends SimpleGameItemHandler {
    public ItemResourceGold(@NotNull ModuleGameItems module) {
        super(module, BedwarsItem.RESOURCE_GOLD.toString());
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public String getDescription() {
        return "Used in shop to gain materials";
    }

    @Override
    public ItemStack getItem(int level) {
        ItemBuilder builder = new ItemBuilder(Material.GOLD_INGOT, 1).setName(ChatColor.YELLOW + "Gold");
        for(String lines :  TextUtil.splitIntoLines(this.getDescription(), 20)){
            builder.addLore(ChatColor.GRAY + lines);
        }
        return builder.build();
    }
}
