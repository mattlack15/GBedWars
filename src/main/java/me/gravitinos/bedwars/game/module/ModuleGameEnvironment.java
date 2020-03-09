package me.gravitinos.bedwars.game.module;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.game.BedwarsMapPointTracker;
import me.gravitinos.bedwars.game.BedwarsTeam;
import me.gravitinos.bedwars.game.module.generator.Generator;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.handler.GameHandler;
import me.gravitinos.bedwars.gamecore.module.GameModule;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ModuleGameEnvironment extends GameModule {

    public static final String PLAYER_PLACED_BLOCK_META = "BW_PLAYER_PLACED";

    private ArrayList<BlockState> revert = new ArrayList<>();

    private CuboidRegion region;

    public ModuleGameEnvironment(@NotNull GameHandler gameHandler, @NotNull CuboidRegion region) {
        super(gameHandler, "PLAYER_BLOCK_HANDLER");
        this.region = region;

        //Beds
        for(int i = 0; i < BedwarsTeam.values().length; i++){
            Location l = ((BedwarsHandler)getGameHandler()).getPointTracker().getBed(BedwarsTeam.values()[i]);
            l.getBlock().setType(Material.GLASS);
        }
    }

    /**
     * Add a block state that will be updated on game end
     *
     * @param state block state
     */
    public void addRevertingBlockState(@NotNull BlockState state) {
        this.revert.add(state);
    }

    /**
     * Reverts all player placed and broken blocks
     */
    public void revertBlocks() {
        for (int i = revert.size() - 1; i >= 0; i--) {
            revert.get(i).update(true);
        }
        revert.clear();
    }

    @EventSubscription
    private void onHunger(FoodLevelChangeEvent event) {
        if (getGameHandler().isPlaying(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }

    }

    @EventSubscription
    private void onPickupItem(EntityPickupItemEvent event){
        if(getGameHandler().isPlaying(event.getEntity().getUniqueId())){
            if(((BedwarsHandler)getGameHandler()).isRespawning(event.getEntity().getUniqueId())){
                event.setCancelled(true);
            }
        }
    }

    @EventSubscription
    private void onBreak(BlockBreakEvent event) {
        if (getGameHandler().isPlaying(event.getPlayer().getUniqueId())) {
            Location loc = event.getBlock().getLocation();
            if (region.contains(new com.sk89q.worldedit.Vector(loc.getX(), loc.getY(), loc.getZ())) && (region.getWorld() == null || loc.getWorld().getName().equals(region.getWorld().getName()))) {
                if (event.getBlock().getType().equals(Material.LONG_GRASS) || event.getBlock().getType().equals(Material.RED_ROSE)) {
                    revert.add(event.getBlock().getState()); //Let them break it, and add to revert
                    event.setDropItems(false);
                    return;
                }

                //Check for beds
                for(int i = 0; i < BedwarsTeam.values().length; i++){
                    Location l = ((BedwarsHandler)getGameHandler()).getPointTracker().getBed(BedwarsTeam.values()[i]);
                    if(event.getBlock().getLocation().equals(l)){
                        BedwarsTeam team = BedwarsTeam.getTeam(((BedwarsHandler)getGameHandler()).getTeamManagerModule().getTeam(event.getPlayer().getUniqueId()));

                        if(team == BedwarsTeam.values()[i]){
                            ((BedwarsHandler)getGameHandler()).sendGameMessage(event.getPlayer(), "You cannot break your own bed", "Anti-Friendly Fire");
                            event.setCancelled(true);
                            return;
                        }

                        ((BedwarsHandler)getGameHandler()).sendGameMessage(BedwarsTeam.values()[i].toString().toUpperCase() + "'s bed has been broken by " + event.getPlayer().getName() + ", their players can no longer respawn!", "Game");
                        ((BedwarsHandler)getGameHandler()).setBedBroken(BedwarsTeam.values()[i], true);
                        return;
                    }
                }

                if (!event.getBlock().hasMetadata(PLAYER_PLACED_BLOCK_META)) {
                    event.setCancelled(true);
                    return;
                }

            }
        }
    }

    @EventSubscription
    private void onPlace(BlockPlaceEvent event) {
        if (getGameHandler().isPlaying(event.getPlayer().getUniqueId())) {
            if (this.isInRestrictedBuildingArea(event.getBlock().getLocation())) {
                event.setCancelled(true);
                ((BedwarsHandler)getGameHandler()).sendGameMessage( "You cannot place blocks here!", "Game");
                return;
            }
            event.getBlock().setMetadata(PLAYER_PLACED_BLOCK_META, new FixedMetadataValue(CoreHandler.main, true));
            revert.add(event.getBlockReplacedState()); //Add block to revert
        }
    }

    /**
     * Checks if a location is in a restricted block placement area
     * @param location Location to check
     * @return Result
     */
    public boolean isInRestrictedBuildingArea(Location location) {
        Vector vector = new Vector(location.getX(), location.getY(), location.getZ());;
        BedwarsMapPointTracker tracker = ((BedwarsHandler)getGameHandler()).getPointTracker();

        //Base generators
        for(Location gens : tracker.getBaseGens()){
            Vector vec = new Vector(gens.getX(), gens.getY()+1, gens.getZ());
            if(vector.distanceSq(vec) < Math.pow(1.9, 2)){
                return true;
            }
        }

        //Outer generators
        for(Location gens : tracker.getOuterGens()){
            Vector vec = new Vector(gens.getX(), gens.getY()+1, gens.getZ());
            if(vector.distanceSq(vec) < Math.pow(3.9, 2)){
                return true;
            }
        }

        //Mid generators
        for(Location gens : tracker.getMidGens()){
            Vector vec = new Vector(gens.getX(), gens.getY()+1, gens.getZ());
            if(vector.distanceSq(vec) < Math.pow(3.9, 2)){
                return true;
            }
        }

        //Shops
        for(int i = 0; i < BedwarsTeam.values().length; i++){
            Location shop = tracker.getShop(BedwarsTeam.values()[i]);
            Vector vec = new Vector(shop.getX(), shop.getY()+1, shop.getZ());
            if(vector.distanceSq(vec) < Math.pow(3.1, 2)){
                return true;
            }
        }

        return false;
    }

    @EventSubscription
    private void onItemDespawn(ItemDespawnEvent event){
        Location loc = event.getLocation();
        Vector vec = new Vector(loc.getX(), loc.getY(), loc.getZ());
        if(((BedwarsHandler)getGameHandler()).getMapRegion().contains(vec)){
            event.setCancelled(true);
        }
    }
}
