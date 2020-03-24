package me.gravitinos.bedwars.game.module.generator;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GeneratorDrop {
    public double countdown;
    private ItemStack stack;
    private double interval;
    public GeneratorDrop(@NotNull ItemStack drop, double interval){
        this.interval = interval;
        this.stack = drop;
        this.countdown = interval;
    }

    public double getInterval() {
        return interval;
    }

    public ItemStack getDrop() {
        return stack;
    }
}
