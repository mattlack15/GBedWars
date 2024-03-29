package me.gravitinos.minigame.gamecore.module;

import com.google.common.collect.Lists;
import me.gravitinos.minigame.gamecore.party.BaseParty;
import me.gravitinos.minigame.gamecore.util.EventSubscription;
import me.gravitinos.minigame.gamecore.util.EventSubscriptions;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class GameHandler {

    private ArrayList<GameModule> modules = new ArrayList<>();
    private int maxGameLengthSeconds;
    private String gameName;

    public GameHandler(String gameName, int maxGameLengthSeconds) {
        EventSubscriptions.instance.subscribe(this);
        EventSubscriptions.instance.abstractSubscribe(this, GameHandler.class);
        this.gameName = gameName;
        this.maxGameLengthSeconds = maxGameLengthSeconds;
    }

    /**
     * Get the name of the game
     *
     * @return
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Get the maximum length of the game in seconds
     *
     * @return Max length of game in seconds, may be -1 for undefined
     */
    public int getMaxGameLengthSeconds() {
        return this.maxGameLengthSeconds;
    }

    /**
     * Gets the modules in this game
     *
     * @return List of the modules in this game
     */
    public ArrayList<GameModule> getModules() {
        return Lists.newArrayList(this.modules);
    }

    /**
     * Adds a module
     *
     * @param module The module to add
     */
    public void addModule(GameModule module) {
        if (!this.modules.contains(module)) {
            module.setGameHandler(this);
            this.modules.add(module);
        }
    }

    /**
     * Get a module from its type
     *
     * @param type The type
     * @return Module of that type
     */
    public <T extends GameModule> T getModule(@NotNull Class<T> type) {
        for (GameModule modules : this.modules) {
            if (type.isAssignableFrom(modules.getClass())) {
                return (T) modules;
            }
        }
        return null;
    }

    /**
     * Get a module from its name
     *
     * @param name The name of the module
     * @return The module
     */
    public GameModule getModule(@NotNull String name) {
        for (GameModule modules : this.modules) {
            if (name.equals(modules.getName())) {
                return modules;
            }
        }
        return null;
    }

    /**
     * Removes a module
     *
     * @param module The module to remove
     */
    public void removeModule(GameModule module) {
        module.disable();
        module.setGameHandler(null);
        this.modules.remove(module);
    }

    protected void clearModules(){
        this.disableAllModules();
        this.modules.forEach(m -> m.setGameHandler(null));
        this.modules.clear();
    }

    protected void disableAllModules(){
        this.modules.forEach(m -> {
            if(m.isEnabled()) m.disable();
        });
    }

    protected void enableAllModules(){
        this.modules.forEach(m -> {
            if(!m.isEnabled()) m.enable();
        });
    }

    /**
     * Don't manually call this method unless you don't use a game queue to start this game
     * This method is called when there is 10 seconds or less until the game is started by a queue
     * This method should/will be called in the main thread
     * NOTE: this will not be called if you manually invoke start()
     * @return Whether or not the game queue should be stopped (whether the pre-startup was successful or not)
     */
    public abstract boolean onPreStart();

    // Player Management

    /**
     * Starts the game
     * NOTE: don't forget to invoke onPreStart before invoking this method
     * @see #onPreStart()
     * @param players List of players that will be playing
     * @return Future that completes with a result representing whether the game was started or not
     */
    public abstract CompletableFuture<Boolean> start(ArrayList<BaseParty> players);

    /**
     * Stops the game
     *
     * @param stopMessage Message to be displayed after stopping
     * @param reason      The reason for stopping the game
     * @return Future
     */
    public abstract CompletableFuture<Void> stop(String stopMessage, GameStopReason reason);

    /**
     * Kick a player
     *
     * @param player The player to kick
     */
    public abstract void kickPlayer(UUID player);

    /**
     * Add a player to the game
     *
     * @param player The player to add
     * @return Whether the player was added or not
     */
    public abstract boolean addPlayer(UUID player);

    /**
     * Add a spectator to the game
     *
     * @param player The player to add as a spectator
     * @return Whether to player was added as a spectator or not
     */
    public abstract boolean addSpectator(UUID player);

    /**
     * Kick a spectator
     *
     * @param player The spectator to kick
     */
    public abstract void kickSpectator(UUID player);

    /**
     * Gets if a player is playing in this game
     *
     * @param player The player to check for
     * @return Whether that player is playing in this game
     */
    public abstract boolean isPlaying(UUID player);

    /**
     * Gets if a player is spectating this game
     *
     * @param player The player to check for
     * @return Whether that player is spectating this game
     */
    public abstract boolean isSpectating(UUID player);

    /**
     * Gets the players in this game
     *
     * @return Players
     */
    public abstract ArrayList<UUID> getPlayers();

    @EventSubscription
    private void onLeave(PlayerQuitEvent event){
        this.kickPlayer(event.getPlayer().getUniqueId());
        this.kickSpectator(event.getPlayer().getUniqueId());
    }

    /**
     * Gets the spectators of this game
     *
     * @return Spectators
     */
    public abstract ArrayList<UUID> getSpectators();

    /**
     * Gets if the game is running
     *
     * @return Whether or not the game is running
     */
    public abstract boolean isRunning();
}
