package me.gravitinos.minigame;

import com.google.common.collect.Lists;
import me.gravitinos.minigame.gamecore.module.GameHandler;
import me.gravitinos.minigame.gamecore.module.GameStopReason;
import me.gravitinos.minigame.gamecore.util.EventSubscription;
import me.gravitinos.minigame.gamecore.util.EventSubscriptions;
import net.minecraft.server.v1_12_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldSaveEvent;

import java.io.File;

public abstract class Minigame extends GameHandler {
    public Minigame(String gameName, int maxGameLengthSeconds) {
        super(gameName, maxGameLengthSeconds);
        EventSubscriptions.instance.abstractSubscribe(this, Minigame.class);
    }

    private String worldName = null;

    public abstract void setMap(File mapConfigFile);


    protected void enterWorldGameState(World world) {
        this.worldName = world.getName();
        world.setAutoSave(false);
        world.setKeepSpawnInMemory(false);
    }

    private String allowUnloading = null;

    protected void exitWorldGameState(World world, Location playerTeleportLocation) {
        long ms = System.currentTimeMillis();

        this.worldName = null;

        world.setAutoSave(false);
        world.setKeepSpawnInMemory(false);

        world.getPlayers().forEach(p -> {
            MinigameMessenger.msgPlayerWarning(p, "This world is resetting... brace for teleportation!");
            p.teleport(playerTeleportLocation);
        });

        world.getPlayers().clear();

        int attemptedUnload = world.getLoadedChunks().length;
        int actuallyUnloaded = 0;

        world.setAutoSave(false);
        world.setKeepSpawnInMemory(false);

        allowUnloading = world.getName();

        for (Chunk loadedChunk : Lists.newArrayList(world.getLoadedChunks())) {
            if (loadedChunk.unload(false)) {
                actuallyUnloaded++;
            }
            attemptedUnload++;
        }

        if (world.getLoadedChunks().length != 0) {
            int finalAttemptedUnload = attemptedUnload;
            int finalActuallyUnloaded = actuallyUnloaded;
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (p.hasPermission("game.viewdiagnostics")) {
                    MinigameMessenger.msgPlayerWarning(p, "Could not unload map properly! Unloaded " + (finalActuallyUnloaded) + " out of " + finalAttemptedUnload + " chunks.");
                    MinigameMessenger.msgPlayerWarning(p, "There are " + world.getPlayers().size() + " players in the world");
                }
            });
        }
        world.setAutoSave(true);

        Bukkit.getOnlinePlayers().forEach((p) -> MinigameMessenger.msgPlayerGame(p, "Took " + (System.currentTimeMillis() - ms) + "ms to reset world", "&cDebug"));

    }

    @EventSubscription
    private void onChunkUnload(ChunkUnloadEvent event) {
        if (event.getWorld().getName().equals(allowUnloading)) {
            if (event.isCancelled()) {
                Bukkit.getOnlinePlayers().forEach(p -> {
                    if (p.hasPermission("game.viewdiagnostics")) {
                        MinigameMessenger.msgPlayerWarning(p, "Something tried to prevent unloading of chunk");
                    }
                });
            }
            event.setSaveChunk(false);
            event.setCancelled(false);
        }
    }

    @EventSubscription
    private void onWorldSave(WorldSaveEvent event) {
        event.getWorld().setAutoSave(false);
        event.getWorld().setKeepSpawnInMemory(false);
        if (event.getWorld().getName().equals(this.worldName)) {
            Bukkit.getOnlinePlayers().forEach((p) -> {
                if (p.hasPermission("game.viewdiagnostics")) {
                    MinigameMessenger.msgPlayerFatal(p, "Something is saving the game world, set bukkit.yml setting ticks-per > autosave to 0!");
                    MinigameMessenger.msgPlayerFatal(p, "Ending game");
                    this.stop("Fatal Error", GameStopReason.GAME_ERROR);
                }
            });
        }

    }

    @EventSubscription
    private void onWorldInit(WorldInitEvent event) {
        if (event.getWorld().getName().equals(this.worldName)) {
            event.getWorld().setAutoSave(false);
            event.getWorld().setKeepSpawnInMemory(false);
        }

    }

}
