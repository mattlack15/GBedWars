package me.gravitinos.bedwars.game.module.gameitems;

import me.gravitinos.bedwars.gamecore.gameitem.GameItemHandler;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.bedwars.gamecore.gameitem.SimpleGameItemHandler;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import me.gravitinos.bedwars.gamecore.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemResourceEmerald extends SimpleGameItemHandler {
    public ItemResourceEmerald(@NotNull ModuleGameItems module) {
        super(module, BedwarsItem.RESOURCE_EMERALD.toString());
    }

    @Override
    public String getDescription() {
        return "Used in shop to gain materials";
    }

    @Override
    public ItemStack getItem(int level) {
        ItemBuilder builder = new ItemBuilder(Material.EMERALD, 1).setName(ChatColor.GREEN + "Emerald");
        for(String lines :  TextUtil.splitIntoLines(this.getDescription(), 20)){
            builder.addLore(ChatColor.GRAY + lines);
        }
        return builder.build();
    }
}
