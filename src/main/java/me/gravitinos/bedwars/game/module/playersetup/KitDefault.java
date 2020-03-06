package me.gravitinos.bedwars.game.module.playersetup;

import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class KitDefault extends Kit{
    @Override
    public ItemStack[] getContents() {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        stacks.add(new ItemBuilder(Material.WOOD_SWORD, 1).setUnbreakable(true).build());
        return stacks.toArray(new ItemStack[0]);
    }
}
