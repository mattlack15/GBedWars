package me.gravitinos.bedwars.game.module;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.gravitinos.bedwars.game.SpigotBedwars;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.handler.GameHandler;
import me.gravitinos.bedwars.gamecore.module.GameModule;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.LazyMetadataValue;
import org.jetbrains.annotations.NotNull;

public class ModulePlayerBlockHandler extends GameModule {

    public static final String PLAYER_PLACED_BLOCK_META = "BW_PLAYER_PLACED";

    private CuboidRegion region;

    public ModulePlayerBlockHandler(@NotNull GameHandler gameHandler, @NotNull CuboidRegion region) {
        super(gameHandler, "PLAYER_BLOCK_HANDLER");
        this.region = region;
    }

    @EventSubscription
    private void onBreak(BlockBreakEvent event){
        if(getGameHandler().isPlaying(event.getPlayer().getUniqueId())){
            Location loc = event.getBlock().getLocation();
            if(region.contains(new com.sk89q.worldedit.Vector(loc.getX(), loc.getY(), loc.getZ())) && (region.getWorld() == null || loc.getWorld().getName().equals(region.getWorld().getName()))){
                if(event.getBlock().getType().equals(Material.LONG_GRASS) || event.getBlock().getType().equals(Material.RED_ROSE)){
                    return;
                }
                if(!event.getBlock().hasMetadata(PLAYER_PLACED_BLOCK_META)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventSubscription
    private void onPlace(BlockPlaceEvent event){
        if(getGameHandler().isPlaying(event.getPlayer().getUniqueId())){
            event.getBlock().setMetadata(PLAYER_PLACED_BLOCK_META, new FixedMetadataValue(CoreHandler.main, true));
        }
    }
}
