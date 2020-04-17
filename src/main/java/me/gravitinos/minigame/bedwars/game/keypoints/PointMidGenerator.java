package me.gravitinos.minigame.bedwars.game.keypoints;

import me.gravitinos.minigame.gamecore.map.MapBlockIdentity;
import me.gravitinos.minigame.gamecore.map.MapKeyPoint;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PointMidGenerator extends MapKeyPoint {
    public PointMidGenerator(@NotNull Consumer<Location> locationConsumer) {
        super("MID_GENERATOR", locationConsumer, new MapBlockIdentity(Material.BEDROCK, 0));
        this.addIdentifyingPoint(new Vector(0,1,0), new MapBlockIdentity(Material.SIGN_POST,-1, "MID_GEN"));
    }
}
