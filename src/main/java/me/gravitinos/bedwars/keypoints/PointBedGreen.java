package me.gravitinos.bedwars.keypoints;

import me.gravitinos.gamecore.map.MapBlockIdentity;
import me.gravitinos.gamecore.map.MapKeyPoint;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PointBedGreen extends MapKeyPoint {
    public PointBedGreen(@NotNull Consumer<Location> locationConsumer) {
        super("BED_BLUE", locationConsumer, new MapBlockIdentity(Material.WOOL.getId(), 5));
        this.addIdentifyingPoint(new Vector(0,1,0), new MapBlockIdentity(Material.GOLD_PLATE.getId(), 0));
        this.addIdentifyingPoint(new Vector(1, 0, 0), new MapBlockIdentity(Material.STONE_SLAB2.getId(), 0));
        this.addIdentifyingPoint(new Vector(-1, 0, 0), new MapBlockIdentity(Material.STONE_SLAB2.getId(), 0));
        this.addIdentifyingPoint(new Vector(0, 0, 1), new MapBlockIdentity(Material.STONE_SLAB2.getId(), 0));
        this.addIdentifyingPoint(new Vector(0, 0, -1), new MapBlockIdentity(Material.STONE_SLAB2.getId(), 0));
    }
}
