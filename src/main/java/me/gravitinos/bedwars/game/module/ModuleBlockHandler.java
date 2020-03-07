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
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.LazyMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ModuleBlockHandler extends GameModule {

    public static final String PLAYER_PLACED_BLOCK_META = "BW_PLAYER_PLACED";

    private ArrayList<BlockState> revert = new ArrayList<>();

    private CuboidRegion region;

    public ModuleBlockHandler(@NotNull GameHandler gameHandler, @NotNull CuboidRegion region) {
        super(gameHandler, "PLAYER_BLOCK_HANDLER");
        this.region = region;
    }

    /**
     * Add a block state that will be updated on game end
     * @param state block state
     */
    public void addRevertingBlockState(@NotNull BlockState state){
        this.revert.add(state);
    }

    /**
     * Reverts all player placed and broken blocks
     */
    public void revertBlocks(){
        for(int i = revert.size()-1; i >= 0; i--){
            revert.get(i).update(true);
        }
        revert.clear();
    }

    @EventSubscription
    private void onBreak(BlockBreakEvent event){
        if(getGameHandler().isPlaying(event.getPlayer().getUniqueId())){
            Location loc = event.getBlock().getLocation();
            if(region.contains(new com.sk89q.worldedit.Vector(loc.getX(), loc.getY(), loc.getZ())) && (region.getWorld() == null || loc.getWorld().getName().equals(region.getWorld().getName()))){
                if(event.getBlock().getType().equals(Material.LONG_GRASS) || event.getBlock().getType().equals(Material.RED_ROSE)){
                    revert.add(event.getBlock().getState()); //Let them break it, and add to revert
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
            revert.add(event.getBlockReplacedState()); //Add block to revert
        }
    }
}
