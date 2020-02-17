package me.gravitinos.gamecore.handler;

import com.google.common.collect.Lists;
import me.gravitinos.gamecore.module.GameModule;
import me.gravitinos.gamecore.util.EventSubscription;
import me.gravitinos.gamecore.util.EventSubscriptions;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class GameHandler {

    private ArrayList<GameModule> modules = new ArrayList<>();
    private int maxGameLengthSeconds;
    private String gameName;

    public GameHandler(String gameName, int maxGameLengthSeconds){
        EventSubscriptions.instance.subscribe(this);
        this.gameName = gameName;
        this.maxGameLengthSeconds = maxGameLengthSeconds;
    }

    public String getGameName() {
        return gameName;
    }

    public int getMaxGameLengthSeconds(){
        return this.maxGameLengthSeconds;
    }

    /**
     * Gets the modules in this game
     * @return List of the modules in this game
     */
    public ArrayList<GameModule> getModules() {
        return Lists.newArrayList(this.modules);
    }

    /**
     * Adds a module
     * @param module The module to add
     */
    public void addModule(GameModule module){
        if(!this.modules.contains(module)) {
            this.modules.add(module);
        }
    }

    /**
     * Removes a module
     * @param module The module to remove
     */
    public void removeModule(GameModule module){
        this.modules.remove(module);
    }

    // Player Management

    /**
     * Starts the game
     * @param players List of players that will be playing
     * @return Future that completes with a result representing whether the game was started or not
     */
    public abstract CompletableFuture<Boolean> start(ArrayList<UUID> players);

    /**
     * Stops the game
     * @param stopMessage Message to be displayed after stopping
     * @param reason The reason for stopping the game
     * @return Future
     */
    public abstract CompletableFuture<Void> stop(String stopMessage, GameStopReason reason);

    /**
     * Kick a player
     * @param player The player to kick
     */
    public abstract void kickPlayer(UUID player);

    /**
     * Add a player to the game
     * @param player The player to add
     * @return Whether the player was added or not
     */
    public abstract boolean addPlayer(UUID player);

    /**
     * Add a spectator to the game
     * @param player The player to add as a spectator
     * @return Whether to player was added as a spectator or not
     */
    public abstract boolean addSpectator(UUID player);

    /**
     * Kick a spectator
     * @param player The spectator to kick
     */
    public abstract void kickSpectator(UUID player);

    /**
     * Gets if a player is playing in this game
     * @param player The player to check for
     * @return Whether that player is playing in this game
     */
    public abstract boolean isPlaying(UUID player);

    /**
     * Gets if the game is running
     * @return Whether or not the game is running
     */
    public abstract boolean isRunning();
}
