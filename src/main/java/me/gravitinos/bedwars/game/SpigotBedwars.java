package me.gravitinos.bedwars.game;

import com.sk89q.worldedit.regions.CuboidRegion;
import me.gravitinos.bedwars.game.command.CommandBW;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.handler.GameStopReason;
import me.gravitinos.bedwars.gamecore.map.MapHandler;
import me.gravitinos.bedwars.gamecore.queue.GameQueue;
import me.gravitinos.bedwars.gamecore.util.SyncProgressReport;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SpigotBedwars extends JavaPlugin implements Listener {
    public static SpigotBedwars instance;
    public static BedwarsHandler bedwarsHandler = null;
    public static GameQueue queue = null;

    public static final String PLUGIN_PREFIX = ChatColor.translateAlternateColorCodes('&', "&e&lSoraxus BedWars &f> &7");

    @Override
    public void onEnable() {
        instance = this;
        new Files();
        new CoreHandler(this);
        this.saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(instance, instance);
        new CommandBW();
    }

    @Override
    public void onDisable() {
        if (bedwarsHandler != null) {
            bedwarsHandler.stop("Disabled", GameStopReason.GAME_END);
        }
    }

    public ArrayList<File> getMapFiles() {
        File[] files = Files.MAPS_FOLDER.listFiles();
        if (files == null) {
            return new ArrayList<>();
        }
        ArrayList<File> files1 = new ArrayList<>();
        for (File file : files) {
            if (file.getName().endsWith(".yml")) {
                files1.add(file);
            }
        }
        return files1;
    }

    public SyncProgressReport<File> createMap(CuboidRegion region, String name) {
        File file = new File(Files.MAPS_FOLDER.getAbsolutePath(), name + ".yml");
        SyncProgressReport<File> progressReport = new SyncProgressReport<>("Map Creation");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            progressReport.getFuture().complete(null);
            return progressReport;
        }
        BedwarsMapDataHandler dataHandler = new BedwarsMapDataHandler(file);
        BedwarsMapPointTracker pointTracker = new BedwarsMapPointTracker(dataHandler, false);
        MapHandler mapHandler = new MapHandler();
        pointTracker.addKeyPointsToMapHandler(mapHandler);

        SyncProgressReport mapProgress = mapHandler.findKeyPoints(region, true);
        mapProgress.addListener((v) -> progressReport.setPercentProgress((double) v));

        CoreHandler.instance.getAsyncExecutor().execute(() -> {
            try {
                mapProgress.getFuture().get();
                pointTracker.export(dataHandler);
            } catch (Exception e) {
                file.delete();
                e.printStackTrace();
                progressReport.getFuture().complete(null);
            }
            progressReport.setPercentProgress(1);
            progressReport.getFuture().complete(file);
        });
        return progressReport;
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        BedwarsPlayer.getPlayer(event.getUniqueId());
    }

}
