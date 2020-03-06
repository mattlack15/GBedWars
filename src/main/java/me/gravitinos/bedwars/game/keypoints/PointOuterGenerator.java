package me.gravitinos.bedwars.game.keypoints;

import me.gravitinos.bedwars.gamecore.map.MapBlockIdentity;
import me.gravitinos.bedwars.gamecore.map.MapKeyPoint;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PointOuterGenerator extends MapKeyPoint {
    public PointOuterGenerator(@NotNull Consumer<Location> locationConsumer) {
        super("MID_GENERATOR", locationConsumer, new MapBlockIdentity(Material.BEDROCK, 0));
        this.addIdentifyingPoint(new Vector(0,1,0), new MapBlockIdentity(Material.SIGN_POST,-1, "OUTER_GEN"));
    }
}
