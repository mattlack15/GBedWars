package me.gravitinos.minigame.bedwars.game;

import com.sk89q.worldedit.regions.CuboidRegion;
import me.gravitinos.minigame.bedwars.anticheat.SpigotAC;
import me.gravitinos.minigame.bedwars.game.command.CommandBW;
import me.gravitinos.minigame.bedwars.game.command.common.CommandParty;
import me.gravitinos.minigame.bedwars.game.module.playersetup.KitSleeper;
import me.gravitinos.minigame.gamecore.*;
import me.gravitinos.minigame.gamecore.data.Kit;
import me.gravitinos.minigame.gamecore.map.MapHandler;
import me.gravitinos.minigame.gamecore.queue.GameQueue;
import me.gravitinos.minigame.gamecore.util.ConfigProtocol;
import me.gravitinos.minigame.gamecore.util.EventSubscriptions;
import me.gravitinos.minigame.gamecore.util.SyncProgressReport;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SpigotBedwars extends GameServerManager<BedwarsHandler> implements Listener {
    public static SpigotBedwars instance;
    public static BedwarsHandler bedwarsHandler;
    public static Lobby<BedwarsHandler> lobbyHandler = null;
    public static GameQueue queue = null;

    public static final String PLUGIN_PREFIX = ChatColor.translateAlternateColorCodes('&', "&e&lSoraxus BedWars &f> &7");

    @Override
    public void onEnable() {
        instance = this;

        if(!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")){
            Bukkit.broadcastMessage(ChatColor.RED + "ProtocolLib must be installed and enabled!");
        }

        new Files();

        EventSubscriptions.instance.subscribe(this);

        new CommandBW();
        new CommandParty();

        //Built-In AC
        new SpigotAC();

        //Kits
        ArrayList<Kit> kits = new ArrayList<>();

        kits.add(new KitSleeper());

        bedwarsHandler = new BedwarsHandler(null);

        Location lobbySpawnLocation = ConfigProtocol.loadLocation(getConfig().getConfigurationSection("lobbySpawnLocation"));

        lobbyHandler = new SimpleLobby<>(bedwarsHandler, lobbySpawnLocation, kits);

    }

    @Override
    public void onDisable() {
        this.getLobby().disable();
    }

    @Override
    public BedwarsHandler getGame() {
        return lobbyHandler.getGame();
    }

    @Override
    public Lobby<BedwarsHandler> getLobby() {
        return lobbyHandler;
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

    @Override
    public int getMaxPlayers() {
        return 16;
    }

    @Override
    public String getGameName() {
        return "Bedwars";
    }
}
