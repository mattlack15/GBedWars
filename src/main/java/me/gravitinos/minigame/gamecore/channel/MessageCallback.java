package me.gravitinos.minigame.gamecore.channel;

import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.IOException;

public interface MessageCallback {
    /**
     * Get the sub channel to listen through
     */
    String getSubChannel();
    boolean test(Player receiver, DataInputStream input) throws IOException;
    boolean accept(Player receiver, DataInputStream input) throws IOException;
}
