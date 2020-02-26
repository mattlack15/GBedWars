package me.gravitinos.bedwars;

import me.gravitinos.gamecore.CoreHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotBedwars extends JavaPlugin {
    public static SpigotBedwars instance;
    public static BedwarsHandler bedwarsHandler;

    @Override
    public void onEnable() {
        instance = this;
        new Files();
        new CoreHandler(this);
        this.saveDefaultConfig();
        new BedwarsMapDataHandler();
        bedwarsHandler = new BedwarsHandler();
    }

}
