package me.gravitinos.minigame.gamecore.map;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.gravitinos.minigame.gamecore.CoreHandler;
import me.gravitinos.minigame.gamecore.module.GameHandler;
import me.gravitinos.minigame.gamecore.module.GameModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

public class ModuleBorder extends GameModule {

    private CuboidRegion region;
    private Consumer<Player> action;

    /**
     * @param gameHandler Game Handler
     * @param region Map Region (WorldEdit Region)
     * @param action (Action to do when player is found outside border
     */
    public ModuleBorder(@NotNull GameHandler gameHandler, @NotNull CuboidRegion region, Consumer<Player> action) {
        super("BORDER");
        this.region = region;
        this.action = action;
    }

    private BukkitRunnable getTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID players : getGameHandler().getPlayers()) {
                    Player player = Bukkit.getPlayer(players);
                    if (region == null || player == null) {
                        return;
                    }
                    if (!region.contains(new Vector(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()))) {
                        action.accept(player);
                    }
                }
                for (UUID spectators : getGameHandler().getSpectators()) {
                    Player player = Bukkit.getPlayer(spectators);
                    if (!region.contains(new Vector(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()))) {
                        action.accept(player);
                    }
                }
            }
        };
    }

    private int currentTaskId = -1;

    @Override
    public void enable() {
        super.enable();
        currentTaskId = getTask().runTaskTimer(CoreHandler.main, 0, 2).getTaskId();
    }

    @Override
    public void disable() {
        super.disable();
        if(currentTaskId != -1)
            Bukkit.getScheduler().cancelTask(currentTaskId);
    }
}
