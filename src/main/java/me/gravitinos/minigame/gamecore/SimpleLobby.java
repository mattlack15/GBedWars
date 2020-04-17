package me.gravitinos.minigame.gamecore;

import me.gravitinos.minigame.Minigame;
import me.gravitinos.minigame.gamecore.data.Kit;
import me.gravitinos.minigame.gamecore.data.MiniPlayer;
import me.gravitinos.minigame.gamecore.module.GameHandler;
import me.gravitinos.minigame.gamecore.party.BaseParty;
import me.gravitinos.minigame.gamecore.queue.GameQueue;
import me.gravitinos.minigame.gamecore.scoreboard.ModuleScoreboard;
import me.gravitinos.minigame.gamecore.scoreboard.SBElement;
import me.gravitinos.minigame.gamecore.scoreboard.SBScope;
import me.gravitinos.minigame.gamecore.util.EventSubscription;
import me.gravitinos.minigame.gamecore.util.EventSubscriptions;
import net.minecraft.server.v1_12_R1.ChunkProviderServer;
import net.minecraft.server.v1_12_R1.IChunkLoader;
import net.minecraft.server.v1_12_R1.RegionFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.SimplePluginManager;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

/**
 * This class handles the server while it is in the waiting lobby state
 */
public class SimpleLobby<T extends Minigame> implements Lobby<T> {
    private ModuleScoreboard board = new ModuleScoreboard("&b&lSoraxus &eStarting in ", SBScope.EVERYONE);

    private ArrayList<UUID> inLobby = new ArrayList<>();

    private GameQueue queue;

    private T handler;

    private ArrayList<Kit> kitHandlers;

    private Location spawnLocation;

    /**
     *
     * @param handler
     * @param kitHandlers The kit handlers, first one in the list is the default kit
     */
    public SimpleLobby(T handler, Location spawnLocation, ArrayList<Kit> kitHandlers) {

        if(kitHandlers.size() < 1){
            throw new IllegalArgumentException("kitHandlers must have a size above 0!");
        }

        queue = new GameQueue(handler, 20);
        queue.addAction(this::disable);
        queue.setShowActionBar(true);
        queue.setActionBarMessage("&cBed wars starting in &f<timeLeftSeconds> seconds &7- &e<numQueued>/<maxQueued>");

        this.spawnLocation = spawnLocation;

        this.handler = handler;
        this.kitHandlers = kitHandlers;
        board.setTitle(new SBElement("").setTextGetter(() -> {
            if(queue.isRunning()){
                return "&bSoraxus &fStarting in " + queue.getTimeLeftSeconds();
            } else {
                return "&bSoraxus &fWaiting for players";
            }
        }));

        board.addElement(new SBElement(""));
        board.addElement(new SBElement("&6&lGame"));
        board.addElement(new SBElement(handler.getGameName()));
        board.addElement(new SBElement(""));
        board.addElement(new SBElement("&6&lKit"));
        board.addElement(new SBElement("").setTextGetter((p) -> {
            if(MiniPlayer.getPlayer(p.getUniqueId(), p.getName()).getKit() == null)
                return "none";
            return MiniPlayer.getPlayer(p.getUniqueId(), p.getName()).getKit().getName();
        }));

    }

    @Override
    public T getGame(){
        return this.handler;
    }

    @Override
    public void startQueue(){
        queue.clearQueued();
        this.inLobby.forEach(q -> queue.queuePlayer(q));
        queue.start();
    }

    @Override
    public void cancelQueue(){
        queue.pause();
    }

    @Override
    public void endQueue() {
        queue.end();
    }

    @Override
    public GameQueue getQueue() {
        return this.queue;
    }


    @Override
    public void addPlayer(UUID player){
        MiniPlayer.getPlayer(player, Bukkit.getPlayer(player).getName()).setKit(kitHandlers.get(0));
        inLobby.add(player);
        if(getGame().isRunning()) {
            getGame().addSpectator(player);
        } else {
            queue.queuePlayer(player);
            Bukkit.getPlayer(player).teleport(spawnLocation);
            Bukkit.getPlayer(player).getInventory().clear();
        }
    }

    public Location getSpawnLocation(){
        return this.spawnLocation;
    }

    @Override
    public void removePlayer(UUID player) {
        this.inLobby.remove(player);
    }


    @Override
    public void enable(){
        this.board.enable();
        for(UUID id : inLobby){
            Bukkit.getPlayer(id).teleport(spawnLocation);
        }
    }

    @Override
    public void disable(){
        this.board.disable();
    }

    @Override
    public boolean isPlayerInLobby(UUID player){
        return this.inLobby.contains(player);
    }

    @Override
    public void setGameMap(File map) {
        getGame().setMap(map);
    }


}
