package me.gravitinos.bedwars.keypoints;

import me.gravitinos.gamecore.map.MapBlockIdentity;
import me.gravitinos.gamecore.map.MapKeyPoint;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PointSpawnYellow extends MapKeyPoint {
    public PointSpawnYellow(@NotNull Consumer<Location> locationConsumer) {
        super("SPAWN_YELLOW", locationConsumer, new MapBlockIdentity(Material.WOOL.getId(), 4));
        this.addIdentifyingPoint(new Vector(0,1,0), new MapBlockIdentity(Material.GOLD_PLATE.getId(), 0));
    }
}
