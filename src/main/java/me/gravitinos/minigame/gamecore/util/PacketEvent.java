package me.gravitinos.minigame.gamecore.util;

import org.bukkit.event.Cancellable;

import java.util.UUID;

public class PacketEvent<T> implements Cancellable {
    private T packet;
    private UUID player;
    private boolean canceled = false;

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.canceled = b;
    }

    public PacketEvent(T packet, UUID player){
        this.packet = packet;
        this.player = player;
    }

    public T getPacket() {
        return packet;
    }

    public UUID getPlayer() {
        return player;
    }
}
