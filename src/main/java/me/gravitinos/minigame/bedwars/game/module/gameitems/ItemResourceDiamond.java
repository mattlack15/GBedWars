package me.gravitinos.minigame.bedwars.game.module.gameitems;

import me.gravitinos.minigame.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.minigame.gamecore.gameitem.SimpleGameItemHandler;
import me.gravitinos.minigame.gamecore.util.ItemBuilder;
import me.gravitinos.minigame.gamecore.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemResourceDiamond extends SimpleGameItemHandler {
    public ItemResourceDiamond(@NotNull ModuleGameItems module) {
        super(module, BedwarsItem.RESOURCE_DIAMOND.toString());
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public String getDescription() {
        return "Used in shop to gain upgrades";
    }

    @Override
    public ItemStack getItem(int level) {
        ItemBuilder builder = new ItemBuilder(Material.DIAMOND, 1).setName(ChatColor.AQUA + "Diamond");
        for(String lines :  TextUtil.splitIntoLines(this.getDescription(), 20)){
            builder.addLore(ChatColor.GRAY + lines);
        }
        return builder.build();
    }
}
