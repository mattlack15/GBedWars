package me.gravitinos.minigame.bedwars.game.module.playersetup;

import me.gravitinos.minigame.gamecore.data.Kit;
import me.gravitinos.minigame.gamecore.data.MiniPlayer;
import me.gravitinos.minigame.gamecore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class KitSleeper extends Kit {
    @Override
    public ItemStack[] getContents(MiniPlayer player) {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        stacks.add(new ItemBuilder(Material.WOOD_SWORD, 1).setUnbreakable(true).build());
        return stacks.toArray(new ItemStack[0]);
    }

    @Override
    public String getName() {
        return "Sleeper";
    }
}
