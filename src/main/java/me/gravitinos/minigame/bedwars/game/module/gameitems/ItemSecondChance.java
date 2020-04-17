package me.gravitinos.minigame.bedwars.game.module.gameitems;

import me.gravitinos.minigame.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.minigame.gamecore.gameitem.SimpleGameItemHandler;
import me.gravitinos.minigame.gamecore.util.ItemBuilder;
import me.gravitinos.minigame.gamecore.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemSecondChance extends SimpleGameItemHandler {

    public static final int COOLDOWN_SECONDS = 5;

    private Map<UUID, Long> lastUsed = new HashMap<>();

    public ItemSecondChance(@NotNull ModuleGameItems module) {
        super(module, BedwarsItem.ITEM_SECOND_CHANCE.toString());
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public String getDescription() {
        return getDescriptions();
    }

    public static String getDescriptions() { return "Keeps your inventory on death"; }

    @Override
    public ItemStack getItem(int level) {
        return getItem();
    }

    public static ItemStack getItem(){
        ItemBuilder builder = new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 1).setName(ChatColor.BLUE + "A Second Chance");
        for(String lines :  TextUtil.splitIntoLines(getDescriptions(), 20)){
            builder.addLore(ChatColor.GRAY + lines);
        }
        return builder.build();
    }
}
