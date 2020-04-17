package me.gravitinos.minigame.gamecore;

import me.gravitinos.minigame.gamecore.module.GameHandler;
import me.gravitinos.minigame.gamecore.party.BaseParty;
import me.gravitinos.minigame.gamecore.queue.GameQueue;
import org.bukkit.Location;

import java.io.File;
import java.util.UUID;

public interface Lobby<T extends GameHandler> {

    T getGame();

    void startQueue();

    void cancelQueue();

    /**
     * Basically force starts the game
     */
    void endQueue();

    GameQueue getQueue();

    void addPlayer(UUID player);

    void removePlayer(UUID player);

    boolean isPlayerInLobby(UUID player);

    void setGameMap(File map);

    /**
     * Enables the scoreboard, and other parts of the lobby, this will teleport everyone in the lobby to the lobby spawn point
     */
    void enable();

    /**
     * Disables scoreboard, and other parts of the lobby, allowing the game to take over
     */
    void disable();
}
