package me.gravitinos.bedwars.keypoints;

import me.gravitinos.gamecore.map.MapBlockIdentity;
import me.gravitinos.gamecore.map.MapKeyPoint;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PointShopBlue extends MapKeyPoint {
    public PointShopBlue(@NotNull Consumer<Location> locationConsumer) {
        super("SHOP_RED", locationConsumer, new MapBlockIdentity(Material.OBSIDIAN.getId(), 0));
        this.addIdentifyingPoint(new Vector(0, 1, 0), new MapBlockIdentity(Material.CARPET.getId(), 11));
    }
}
