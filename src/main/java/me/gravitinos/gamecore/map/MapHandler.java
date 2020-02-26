package me.gravitinos.gamecore.map;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapHandler {
    private boolean mapPrePlaced = true;
    private File mapFile = null;
    private ArrayList<MapKeyPoint> keyPoints = new ArrayList<>();

    public MapHandler() {
    }

    public MapHandler(File mapSchematic) {
        this.mapFile = mapSchematic;
    }

    public void addKeyPoint(MapKeyPoint point) {
        this.keyPoints.add(point);
    }

    public void removeKeyPoint(MapKeyPoint point) {
        this.keyPoints.remove(point);
    }

    public ArrayList<MapKeyPoint> getKeyPoints() {
        return Lists.newArrayList(this.keyPoints);
    }

    public boolean findKeyPoints(CuboidRegion region) {
        Map<Vector, BaseBlock> blocks = getBlocks(region);
        if (blocks == null) {
            return false;
        }
        try {
            for (Vector blockPos : blocks.keySet()) {
                for (MapKeyPoint points : keyPoints) {
                    Map<Vector, MapBlockIdentity> identifyingPoints = points.getIdentifyingPoints();
                    boolean matched = true;
                    for (Vector relPos : identifyingPoints.keySet()) {
                        MapBlockIdentity identity = identifyingPoints.get(relPos);
                        BaseBlock rel = blocks.get(blockPos.clone().add(relPos));
                        if (rel.getId() != identity.getId() || rel.getData() != identity.getData()) {
                            matched = false;
                            break;
                        }
                    }
                    if (matched) {
                        points.getLocationConsumer().accept(new Location(Bukkit.getWorld(Objects.requireNonNull(region.getWorld()).getName()), blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Map<Vector, BaseBlock> getBlocks(CuboidRegion region) {
        Map<Vector, BaseBlock> blocks = new HashMap<>();
        World world = region.getWorld();
        if (world == null) {
            return null;
        }
        for (int x = region.getMinimumPoint().getBlockX(); x < region.getMaximumPoint().getBlockX(); x++) {
            for (int y = region.getMinimumPoint().getBlockY(); y < region.getMaximumPoint().getBlockY(); y++) {
                for (int z = region.getMinimumPoint().getBlockZ(); z < region.getMaximumPoint().getBlockZ(); z++) {
                    blocks.put(new Vector(x, y, z), world.getLazyBlock(new com.sk89q.worldedit.Vector(x,y,z)));
                }
            }
        }
        return blocks;
    }
}
