package me.gravitinos.bedwars.gamecore.channel;

import com.google.common.collect.Lists;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.spigotmc.SpigotConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

public class ProxyComm implements PluginMessageListener {

    public static ProxyComm instance;

    public static final String CHANNEL = "BungeeCord";

    private ArrayList<MessageCallback> callbacks = new ArrayList<>();

    private boolean enabled = false;

    public ProxyComm(){
        instance = this;

        if (SpigotConfig.bungee && !Bukkit.getServer().getOnlineMode()) {
            enabled = true; //If this is part of a bungee network, then enable this
        }

        Bukkit.getMessenger().registerOutgoingPluginChannel(CoreHandler.main, CHANNEL);
        Bukkit.getMessenger().registerIncomingPluginChannel(CoreHandler.main, CHANNEL, this);
    }


    /**
     * Register an agent (Also sends the message that the agent handles)
     * @param agent The agent
     */
    public synchronized void register(MessageAgent agent){
        if(!enabled) return;
        this.send(agent);
        if(agent instanceof MessageCallback){
            this.callbacks.add((MessageCallback) agent);
        }
    }

    /**
     * Register a listener
     * @param listener The callback for the listener
     */
    public synchronized void registerListener(MessageCallback listener){
        if(!enabled) return;
        this.callbacks.add(listener);
    }

    /**
     * Sends a message to the proxy
     * @param agent The agent of the message to send
     */
    public synchronized void send(MessageAgent agent){
        if(!enabled) return;

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(b);
        try {
            //Append sub-channel
            stream.writeUTF(agent.getSubChannel());

            //Append payload
            agent.appendPayload(stream);

            //Send
            agent.getPlayer().sendPluginMessage(CoreHandler.main, CHANNEL, b.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public synchronized void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if(!enabled) return;

        if(!channel.equals(CHANNEL)){
            return;
        }

        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(bytes));

        try {
            String subChannel = stream.readUTF();

            stream.mark(/*Param is ignored*/0);

            ArrayList<MessageCallback> toRemove = new ArrayList<>();

            for(MessageCallback callback : Lists.newArrayList(callbacks)){

                if(!callback.getSubChannel().equals(subChannel)){
                    continue;
                }

                stream.reset();

                //Test
                if(callback.test(player, stream)){
                    stream.reset();

                    //Pass it through to the callback
                    if(callback.accept(player, stream)){
                        toRemove.add(callback);
                    }
                }
            }
            this.callbacks.removeAll(toRemove);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Get the UUID of a player from their username
     * @param playerName The username of the player
     * @return The UUID of the player
     */
    public CompletableFuture<UUID> getUUIDOther(String playerName){
        CompletableFuture<UUID> future = new CompletableFuture<>();
        if(!this.isEnabled()){
            future.complete(null);
            return future;
        }
        this.register(new MessageGetUUID(playerName, future::complete));
        return future;
    }

    /**
     * Get the players that are online in a specific server
     * @param server The server
     * @return The players online in that server
     */
    public CompletableFuture<ArrayList<String>> getOnlinePlayers(String server){
        CompletableFuture<ArrayList<String>> future = new CompletableFuture<>();
        if(!this.isEnabled()){
            future.complete(new ArrayList<>());
            return future;
        }
        this.register(new MessageGetOnlinePlayers(server, future::complete));
        return future;
    }
}
