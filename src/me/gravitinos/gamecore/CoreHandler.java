package me.gravitinos.gamecore;

import org.bukkit.plugin.Plugin;

public class CoreHandler {
    public static Plugin main;
    public static CoreHandler instance;
    public CoreHandler(Plugin mainPlugin){
        instance = this;
        main = mainPlugin;
    }
}
