package me.gravitinos.bedwars.game.keypoints;

import me.gravitinos.bedwars.gamecore.map.MapBlockIdentity;
import me.gravitinos.bedwars.gamecore.map.MapKeyPoint;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PointSpawnBlue extends MapKeyPoint {
    public PointSpawnBlue(@NotNull Consumer<Location> locationConsumer) {
        super("SPAWN_BLUE", locationConsumer, new MapBlockIdentity(Material.WOOL.getId(), 11));
        this.addIdentifyingPoint(new Vector(0,1,0), new MapBlockIdentity(Material.GOLD_PLATE.getId(), 0));
    }
}
