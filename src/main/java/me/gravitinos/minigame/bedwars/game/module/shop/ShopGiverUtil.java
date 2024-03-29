package me.gravitinos.minigame.bedwars.game.module.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ShopGiverUtil {

    public static Consumer<Player> getSwordGiver(@NotNull ItemStack give){
        return (p) -> {
            ItemStack[] contents = p.getInventory().getStorageContents();
            for(int i = 0; i < contents.length; i++){
                ItemStack stack = contents[i];
                if(stack == null) continue;
                if(stack.getType().name().endsWith("SWORD")){
                    contents[i] = null;
                }
            }
            p.getInventory().setStorageContents(contents);
            p.getInventory().addItem(give);
        };
    }
}
