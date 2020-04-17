package me.gravitinos.minigame.gamecore.channel;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class MessageGetOnlinePlayers implements MessageAgent, MessageCallback {

    private Consumer<ArrayList<String>> callback;
    private String server;

    public MessageGetOnlinePlayers(String server, Consumer<ArrayList<String>> callback){
        this.callback = callback;
        this.server = server;
    }

    @Override
    public String getSubChannel() {
        return "PlayerList";
    }

    @Override
    public boolean test(Player receiver, DataInputStream input) throws IOException {
        return server.equals(input.readUTF());
    }

    @Override
    public boolean accept(Player receiver, DataInputStream input) throws IOException {
        input.readUTF();
        this.callback.accept(Lists.newArrayList(input.readUTF().split(", ")));
        return true;
    }

    @Override
    public void appendPayload(DataOutputStream outputStream) throws IOException {
        outputStream.writeUTF(server);
    }

    @Override
    public Player getPlayer() {
        return Lists.newArrayList(Bukkit.getOnlinePlayers()).get(0);
    }
}
