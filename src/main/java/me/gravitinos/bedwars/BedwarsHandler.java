package me.gravitinos.bedwars;

import me.gravitinos.gamecore.CoreHandler;
import me.gravitinos.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.gamecore.handler.GameHandler;
import me.gravitinos.gamecore.handler.GameStopReason;
import me.gravitinos.gamecore.map.MapHandler;
import me.gravitinos.gamecore.scoreboard.ModuleScoreboard;
import me.gravitinos.gamecore.scoreboard.SBElement;
import me.gravitinos.gamecore.scoreboard.SBScope;
import me.gravitinos.gamecore.team.ModuleTeamManager;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BedwarsHandler extends GameHandler {

    private boolean running = false;
    private BedwarsMapPointTracker pointTracker = new BedwarsMapPointTracker();
    private MapHandler mapHandler = new MapHandler();

    public BedwarsHandler() {
        super("Bedwars", 3600);
        this.addModule(new ModuleGameItems(this));
        this.addModule(new ModuleTeamManager(this));
        this.addModule(new ModuleScoreboard(this, ChatColor.RED + "BedWars", SBScope.EVERYONE));
        this.getModule(ModuleScoreboard.class).addElement(new SBElement("Test Text " + ChatColor.GRAY + "with colour!"));
    }

    public ModuleGameItems getGameItemsModule(){
        return this.getModule(ModuleGameItems.class);
    }

    public ModuleTeamManager getTeamManagerModule(){
        return this.getModule(ModuleTeamManager.class);
    }

    @Override
    public CompletableFuture<Boolean> start(ArrayList<UUID> players) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        CoreHandler.instance.getAsyncExecutor().execute(() -> {
            //Startup

                //Map Setup
                if(BedwarsMapDataHandler.instance.isConfigured()){
                    this.pointTracker.setBedBLUE(BedwarsMapDataHandler.instance.getBedLocation(BedwarsTeam.BLUE));
                    this.pointTracker.setBedRED(BedwarsMapDataHandler.instance.getBedLocation(BedwarsTeam.RED));
                    this.pointTracker.setBedYELLOW(BedwarsMapDataHandler.instance.getBedLocation(BedwarsTeam.YELLOW));
                    this.pointTracker.setBedGREEN(BedwarsMapDataHandler.instance.getBedLocation(BedwarsTeam.GREEN));

                    this.pointTracker.setBorder1(BedwarsMapDataHandler.instance.getBorder1());
                    this.pointTracker.setBorder2(BedwarsMapDataHandler.instance.getBorder2());

                    this.pointTracker.setMidGens(BedwarsMapDataHandler.instance.getMidGeneratorLocations());
                    this.pointTracker.setOuterGens(BedwarsMapDataHandler.instance.getOuterGeneratorLocations());

                    this.pointTracker.setShopBLUE(BedwarsMapDataHandler.instance.getShopLocation(BedwarsTeam.BLUE));
                    this.pointTracker.setShopRED(BedwarsMapDataHandler.instance.getShopLocation(BedwarsTeam.RED));
                    this.pointTracker.setShopYELLOW(BedwarsMapDataHandler.instance.getShopLocation(BedwarsTeam.YELLOW));
                    this.pointTracker.setShopGREEN(BedwarsMapDataHandler.instance.getShopLocation(BedwarsTeam.GREEN));

                    this.pointTracker.setSpawnpointsBLUE(BedwarsMapDataHandler.instance.getSpawnpoints(BedwarsTeam.BLUE));
                    this.pointTracker.setSpawnpointsRED(BedwarsMapDataHandler.instance.getSpawnpoints(BedwarsTeam.RED));
                    this.pointTracker.setSpawnpointsYELLOW(BedwarsMapDataHandler.instance.getSpawnpoints(BedwarsTeam.YELLOW));
                    this.pointTracker.setSpawnpointsGREEN(BedwarsMapDataHandler.instance.getSpawnpoints(BedwarsTeam.GREEN));

                }

            future.complete(true);
        });
        return future;
    }

    @Override
    public CompletableFuture<Void> stop(String stopMessage, GameStopReason reason) {
        return null;
    }

    @Override
    public void kickPlayer(UUID player) {

    }

    @Override
    public boolean addPlayer(UUID player) {
        return false;
    }

    @Override
    public boolean addSpectator(UUID player) {
        return false;
    }

    @Override
    public void kickSpectator(UUID player) {

    }

    @Override
    public boolean isPlaying(UUID player) {
        return false;
    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
