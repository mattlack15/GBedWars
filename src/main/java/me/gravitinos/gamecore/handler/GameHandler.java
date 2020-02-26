package me.gravitinos.gamecore.handler;

import com.google.common.collect.Lists;
import me.gravitinos.gamecore.module.GameModule;
import me.gravitinos.gamecore.util.EventSubscriptions;
import me.gravitinos.gamecore.util.WeakList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class GameHandler {

    private static WeakList<GameHandler> gameHandlers = new WeakList<>();

    private ArrayList<GameModule> modules = new ArrayList<>();
    private int maxGameLengthSeconds;
    private String gameName;

    public GameHandler(String gameName, int maxGameLengthSeconds){
        EventSubscriptions.instance.subscribe(this);
        this.gameName = gameName;
        this.maxGameLengthSeconds = maxGameLengthSeconds;
        synchronized (GameHandler.class){
            gameHandlers.add(this);
        }
    }

    /**
     * Get the name of the game
     * @return
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Get all existing (constructed) game handlers
     * @return List of them
     */
    public static WeakList<GameHandler> getGameHandlers(){
        return gameHandlers;
    }

    /**
     * Get the maximum length of the game in seconds
     * @return Max length of game in seconds
     */
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
     * Get a module from its type
     * @param type The type
     * @return Module of that type
     */
    public <T extends GameModule> T getModule(@NotNull Class<T> type){
        for(GameModule modules : this.modules){
            if(type.isAssignableFrom(modules.getClass())){
                return (T) modules;
            }
        }
        return null;
    }

    /**
     * Get a module from its name
     * @param name The name of the module
     * @return The module
     */
    public GameModule getModule(@NotNull String name){
        for(GameModule modules : this.modules){
            if(name.equals(modules.getName())){
                return modules;
            }
        }
        return null;
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
