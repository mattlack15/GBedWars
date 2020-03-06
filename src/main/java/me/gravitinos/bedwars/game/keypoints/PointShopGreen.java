package me.gravitinos.bedwars.game.keypoints;

import me.gravitinos.bedwars.gamecore.map.MapBlockIdentity;
import me.gravitinos.bedwars.gamecore.map.MapKeyPoint;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PointShopGreen extends MapKeyPoint {
    public PointShopGreen(@NotNull Consumer<Location> locationConsumer) {
        super("SHOP_RED", locationConsumer, new MapBlockIdentity(Material.OBSIDIAN.getId(), 0));
        this.addIdentifyingPoint(new Vector(0, 1, 0), new MapBlockIdentity(Material.CARPET.getId(), 5));
    }
}
