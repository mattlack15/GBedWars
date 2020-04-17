package me.gravitinos.minigame.bedwars.game.keypoints;

import me.gravitinos.minigame.gamecore.map.MapBlockIdentity;
import me.gravitinos.minigame.gamecore.map.MapKeyPoint;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PointBorder extends MapKeyPoint {
    public PointBorder(@NotNull Consumer<Location> locationConsumer) {
        super("BORDER_MARKER", locationConsumer, new MapBlockIdentity(Material.COAL_BLOCK.getId(), 0));
        this.addIdentifyingPoint(new Vector(0,1,0), new MapBlockIdentity(Material.IRON_PLATE.getId(), 0));
    }
}
