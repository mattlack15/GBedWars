package me.gravitinos.minigame.bedwars.game.keypoints;

import me.gravitinos.minigame.gamecore.map.MapBlockIdentity;
import me.gravitinos.minigame.gamecore.map.MapKeyPoint;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PointBedYellow extends MapKeyPoint {
    public PointBedYellow(@NotNull Consumer<Location> locationConsumer) {
        super("BED_BLUE", locationConsumer, new MapBlockIdentity(Material.WOOL.getId(), 4));
        this.addIdentifyingPoint(new Vector(0,1,0), new MapBlockIdentity(Material.SIGN_POST.getId(), -1, "BED_YELLOW"));
  }
}
