package me.gravitinos.minigame.gamecore.channel;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

public class MessageGetUUID implements MessageAgent, MessageCallback {
    private Consumer<UUID> callback;
    private String name;

    public MessageGetUUID(String name, Consumer<UUID> callback){
        this.callback = callback;
        this.name = name;
    }

    @Override
    public String getSubChannel() {
        return "UUIDOther";
    }

    @Override
    public boolean test(Player receiver, DataInputStream input) throws IOException {
        return name.equals(input.readUTF());
    }

    @Override
    public boolean accept(Player receiver, DataInputStream input) throws IOException {
        input.readUTF();
        this.callback.accept(UUID.fromString(insertDashUUID(input.readUTF())));
        return true;
    }

    @Override
    public void appendPayload(DataOutputStream outputStream) throws IOException {
        outputStream.writeUTF(name);
    }

    @Override
    public Player getPlayer() {
        return Lists.newArrayList(Bukkit.getOnlinePlayers()).get(0);
    }

    public static String insertDashUUID(String uuid) {
        StringBuilder sb = new StringBuilder(uuid);
        sb.insert(8, "-");
        sb.insert(13, "-");
        sb.insert(18, "-");
        sb.insert(23, "-");
        return sb.toString();
    }
}
