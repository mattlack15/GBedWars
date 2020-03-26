package me.gravitinos.bedwars.game.module;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.game.BedwarsMapPointTracker;
import me.gravitinos.bedwars.game.BedwarsTeam;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.module.GameHandler;
import me.gravitinos.bedwars.gamecore.module.GameModule;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import me.gravitinos.bedwars.gamecore.util.EventSubscriptions;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.material.Bed;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

/**
 * This module is used for the game environment, which includes the following
 * - Keeping track of player-modified blocks, and reverting them when the game finishes (on revertBlocks())
 * - Making sure items do not de-spawn
 * - Handling Item pickups, what you can modify in your inventory
 * - And other stuff
 */
public class ModuleGameEnvironment extends GameModule {

    public static final String PLAYER_PLACED_BLOCK_META = "BW_PLAYER_PLACED";

    private ArrayList<BlockState> revert = new ArrayList<>();

    private CuboidRegion region;

    public ModuleGameEnvironment(@NotNull GameHandler gameHandler, @NotNull CuboidRegion region) {
        super(gameHandler, "GAME_ENVIRONMENT");
        this.region = region;

        //Place Beds
        for(int i = 0; i < BedwarsTeam.values().length; i++){
            Location l = ((BedwarsHandler)getGameHandler()).getPointTracker().getBed(BedwarsTeam.values()[i]);
            Vector dirToMid = ((BedwarsHandler)getGameHandler()).getMapRegion().getCenter().setY(l.getY()).subtract(new Vector(l.getX(), l.getY(), l.getZ())).normalize();
            Location b = new Location(l.getWorld(), l.getX() + dirToMid.getX() + 0.5, l.getY() + dirToMid.getY() + 0.5, l.getZ() + dirToMid.getZ() + 0.5);
            BlockState bs1 = l.getBlock().getState();
            bs1.setType(Material.BED_BLOCK);
            Bed bed = (Bed) bs1.getData();
            bed.setFacingDirection(l.getBlock().getFace(b.getBlock()));
            bs1.setData(bed);
            bs1.update(true, false);

            BlockState bs = b.getBlock().getState();
            bs.setType(Material.BED_BLOCK);
            Bed bed2 = (Bed) bs.getData();
            bed2.setHeadOfBed(true);
            bed2.setFacingDirection(l.getBlock().getFace(b.getBlock()));
            bs.update(true, false);

        }

    }

    BukkitRunnable runnable = new BukkitRunnable(){
        @Override
        public void run() {
            if(!getGameHandler().isRunning())
                return;

            for(BedwarsTeam team : BedwarsTeam.values()){
                BedwarsHandler handler = (BedwarsHandler) getGameHandler();
                if(handler.getTeamInfo(team).isBedDestroyed()){
                    continue;
                }
                Location loc = handler.getPointTracker().getBed(team).clone();
                org.bukkit.util.Vector dirToMid = handler.getPointTracker().getMidGens().get(0).toVector().subtract(loc.toVector()).normalize();
                loc.add(0.5, 0, 0.5);
                loc.add(dirToMid.multiply(0.5));

                Random rand = new Random(System.currentTimeMillis());
                for(int i = 0; i < 6; i++) {
                    double x = rand.nextDouble() * 2 - 1;
                    double y = rand.nextDouble() * 1.5;
                    double z = rand.nextDouble() * 2 - 1;

                    loc.getWorld().spawnParticle(Particle.SUSPENDED_DEPTH, loc.clone().add(x, y, z), 1);
                }
            }
        }
    };

    public void enable(){
        super.enable();
        EventSubscriptions.instance.subscribe(this);
        runnable.runTaskTimer(CoreHandler.main, 0, 10);
    }

    public void disable(){
        super.disable();
        EventSubscriptions.instance.unSubscribe(this);
        runnable.cancel();
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
            revert.get(i).removeMetadata(PLAYER_PLACED_BLOCK_META, CoreHandler.main);
        }
        revert.clear();
    }


    @EventSubscription
    private void onHunger(FoodLevelChangeEvent event) {
        if(!this.isEnabled()){
            return;
        }
        if (getGameHandler().isPlaying(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }

    }

    @EventSubscription
    private void onLiquidPlace(PlayerBucketEmptyEvent event){
        if(!this.isEnabled()){
            return;
        }
        if (getGameHandler().isRunning()) {
            if (this.isInRestrictedBuildingArea(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation())) {
                event.setCancelled(true);
                ((BedwarsHandler)getGameHandler()).sendGameMessage(event.getPlayer(), "You cannot place blocks here!", "Game");
                return;
            }
            event.getBlockClicked().getRelative(event.getBlockFace()).setMetadata(PLAYER_PLACED_BLOCK_META, new FixedMetadataValue(CoreHandler.main, true));
            revert.add(event.getBlockClicked().getRelative(event.getBlockFace()).getState()); //Add block to revert
        }
    }

    @EventSubscription
    private void onPickupItem(EntityPickupItemEvent event){
        if(!this.isEnabled()){
            return;
        }
        if(getGameHandler().isPlaying(event.getEntity().getUniqueId())){
            if(((BedwarsHandler)getGameHandler()).isRespawning(event.getEntity().getUniqueId())){
                event.setCancelled(true);
            }
        }
        if(getGameHandler().isSpectating(event.getEntity().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventSubscription
    private void onInventoryClick(InventoryClickEvent event){
        if(!this.isEnabled()){
            return;
        }
        if(getGameHandler().isPlaying(event.getWhoClicked().getUniqueId())){
            if(event.getSlotType() == InventoryType.SlotType.ARMOR){
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventSubscription
    private void onBedSleep(PlayerBedEnterEvent event){
        if(!this.isEnabled()){
            return;
        }
        if (getGameHandler().isPlaying(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventSubscription
    private void onBreak(BlockBreakEvent event) {
        if(!this.isEnabled()){
            return;
        }
        if (getGameHandler().isPlaying(event.getPlayer().getUniqueId())) {
            Location loc = event.getBlock().getLocation();
            if (region.contains(new com.sk89q.worldedit.Vector(loc.getX(), loc.getY(), loc.getZ())) && (region.getWorld() == null || loc.getWorld().getName().equals(region.getWorld().getName()))) {
                if (event.getBlock().getType().equals(Material.LONG_GRASS) || event.getBlock().getType().equals(Material.RED_ROSE)) {
                    revert.add(event.getBlock().getState()); //Let them break it, and add to revert
                    event.setDropItems(false);
                    return;
                }


                Location blockToLookFor = event.getBlock().getLocation();
                if(event.getBlock().getState().getData() instanceof Bed){
                    Bed bed = (Bed)event.getBlock().getState().getData();
                    if(bed.isHeadOfBed()){
                        blockToLookFor = event.getBlock().getRelative(bed.getFacing().getOppositeFace()).getLocation();
                    }
                }
                //Check for beds
                for(int i = 0; i < BedwarsTeam.values().length; i++){
                    Location l = ((BedwarsHandler)getGameHandler()).getPointTracker().getBed(BedwarsTeam.values()[i]);
                    if(blockToLookFor.equals(l)){
                        //Bed broken

                        BedwarsTeam team = BedwarsTeam.getTeam(((BedwarsHandler)getGameHandler()).getTeamManagerModule().getTeam(event.getPlayer().getUniqueId()));

                        if(team == BedwarsTeam.values()[i]){
                            ((BedwarsHandler)getGameHandler()).sendGameMessage(event.getPlayer(), "You cannot break your own bed", "Anti-Friendly Fire");
                            event.setCancelled(true);
                            return;
                        }

                        ((BedwarsHandler)getGameHandler()).setBedBroken(BedwarsTeam.values()[i], true, event.getPlayer().getName());

                        event.setDropItems(false);
                        return;
                    }
                }

                if (!event.getBlock().hasMetadata(PLAYER_PLACED_BLOCK_META)) {
                    event.setCancelled(true);
                    return;
                }

            }
        } else if(getGameHandler().isRunning()){
            if(((BedwarsHandler)getGameHandler()).getMapRegion().contains(new Vector(event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ()))){
                event.setCancelled(true);
            }
        }
    }

    @EventSubscription
    private void onPlace(BlockPlaceEvent event) {
        if(!this.isEnabled()){
            return;
        }
        if (getGameHandler().isRunning()) {
            if (this.isInRestrictedBuildingArea(event.getBlock().getLocation())) {
                event.setCancelled(true);
                ((BedwarsHandler)getGameHandler()).sendGameMessage(event.getPlayer(), "You cannot place blocks here!", "Game");
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
