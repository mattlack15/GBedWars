package me.gravitinos.bedwars.gamecore.channel;

import org.bukkit.entity.Player;

import java.io.DataOutputStream;
import java.io.IOException;

public interface MessageAgent {

    /**
     * Get the sub channel to send through
     */
    String getSubChannel();

    void appendPayload(DataOutputStream outputStream) throws IOException;

    /**
     * Get the player to send the message to
     */
    Player getPlayer();

}
