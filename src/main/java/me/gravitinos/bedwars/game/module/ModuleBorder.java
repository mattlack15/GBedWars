package me.gravitinos.bedwars.game.module;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.handler.GameHandler;
import me.gravitinos.bedwars.gamecore.module.GameModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class ModuleBorder extends GameModule {

    public ModuleBorder(@NotNull GameHandler gameHandler, @NotNull CuboidRegion region) {
        super(gameHandler, "BORDER");
        new BukkitRunnable(){
            @Override
            public void run() {
                for(UUID players : getGameHandler().getPlayers()){
                    Player player = Bukkit.getPlayer(players);
                    if(!region.contains(new Vector(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()))){
                        ((BedwarsHandler)getGameHandler()).killPlayer(players, ChatColor.RED + "Border");
                    }
                }
                for(UUID spectators : getGameHandler().getSpectators()){
                    Player player = Bukkit.getPlayer(spectators);
                    if(!region.contains(new Vector(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()))){
                        ArrayList<Location> midGens = ((BedwarsHandler)getGameHandler()).getPointTracker().getMidGens();
                        if(midGens.size() > 0){
                            player.teleport(midGens.get(0), PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
                        } else {
                            player.teleport(new Location(Bukkit.getWorld(Objects.requireNonNull(region.getWorld()).getName()), region.getCenter().getX(), region.getCenter().getY(), region.getCenter().getZ()), PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
                        }
                    }
                }
            }
        }.runTaskTimer(CoreHandler.main, 0, 2);
    }


}
