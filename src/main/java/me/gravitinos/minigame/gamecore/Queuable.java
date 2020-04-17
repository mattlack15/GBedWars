package me.gravitinos.minigame.gamecore;

import java.util.UUID;

public interface Queuable {
    int getMaxPlayers();
    String getGameName();
    UUID getId();
}
