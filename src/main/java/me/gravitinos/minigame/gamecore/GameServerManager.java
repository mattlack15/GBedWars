package me.gravitinos.minigame.gamecore;

import me.gravitinos.minigame.Minigame;
import me.gravitinos.minigame.SpigotMinigames;
import me.gravitinos.minigame.gamecore.module.GameHandler;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

/**
 * This class MUST have a no-args constructor
 * @param <T>
 */
public abstract class GameServerManager<T extends Minigame> implements Queuable {
    private UUID id = UUID.randomUUID();

    public abstract void onEnable();
    public abstract void onDisable();

    /**
     * Gets the game instance, null if none exists (Just because it returns a gameHandler, doesn't mean the game is running, check game.isRunning())
     * @see GameHandler#isRunning()
     * @return The game instance
     */
    public abstract T getGame();

    public abstract Lobby<T> getLobby();

    public FileConfiguration getConfig(){
        return SpigotMinigames.instance.getConfig();
    }

    public void saveConfig(){
        SpigotMinigames.instance.saveConfig();
    }

    public UUID getId(){
        return id;
    }
}

