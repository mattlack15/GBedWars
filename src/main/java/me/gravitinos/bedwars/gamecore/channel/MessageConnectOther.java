package me.gravitinos.bedwars.gamecore.channel;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.DataOutputStream;
import java.io.IOException;

public class MessageConnectOther implements MessageAgent {

    private String player, server;

    public MessageConnectOther(String playerName, String server){
        this.player = playerName;
        this.server = server;
    }

    @Override
    public String getSubChannel() {
        return "ConnectOther";
    }

    @Override
    public void appendPayload(DataOutputStream outputStream) throws IOException {
        outputStream.writeUTF(player);
        outputStream.writeUTF(server);
    }

    @Override
    public Player getPlayer() {
        return Lists.newArrayList(Bukkit.getOnlinePlayers()).get(0);
    }
}
