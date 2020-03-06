package me.gravitinos.bedwars.game.keypoints;

import me.gravitinos.bedwars.gamecore.map.MapBlockIdentity;
import me.gravitinos.bedwars.gamecore.map.MapKeyPoint;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PointBedGreen extends MapKeyPoint {
    public PointBedGreen(@NotNull Consumer<Location> locationConsumer) {
        super("BED_BLUE", locationConsumer, new MapBlockIdentity(Material.WOOL.getId(), 5));
        this.addIdentifyingPoint(new Vector(0,1,0), new MapBlockIdentity(Material.SIGN_POST.getId(), -1, "BED_GREEN"));
  }
}
