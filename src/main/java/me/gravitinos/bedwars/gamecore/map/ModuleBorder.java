package me.gravitinos.bedwars.gamecore.map;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.module.GameHandler;
import me.gravitinos.bedwars.gamecore.module.GameModule;
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
        super(gameHandler, "BORDER");
        this.region = region;
        this.action = action;
    }

    private BukkitRunnable runnable = new BukkitRunnable(){
        @Override
        public void run() {
            for(UUID players : getGameHandler().getPlayers()){
                Player player = Bukkit.getPlayer(players);
                if(region == null || player == null){
                    return;
                }
                if(!region.contains(new Vector(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()))){
                    action.accept(player);
                }
            }
            for(UUID spectators : getGameHandler().getSpectators()){
                Player player = Bukkit.getPlayer(spectators);
                if(!region.contains(new Vector(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()))){
                    action.accept(player);
                }
            }
        }
    };

    @Override
    public void enable() {
        super.enable();
        runnable.runTaskTimer(CoreHandler.main, 0, 2);
    }

    @Override
    public void disable() {
        super.disable();
        runnable.cancel();
    }
}
