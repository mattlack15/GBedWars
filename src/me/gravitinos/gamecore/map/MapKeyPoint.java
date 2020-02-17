package me.gravitinos.gamecore.map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Location;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class MapKeyPoint {
    private Map<Vector, MapBlockIdentity> identifyingPoints = new HashMap<>();
    private Consumer<Location> locationConsumer;
    private String name;
    public MapKeyPoint(@NotNull String name, @NotNull Consumer<Location> locationConsumer, @NotNull MapBlockIdentity originIdentifyingPoint){
        this.addIdentifyingPoint(new Vector(0,0,0), originIdentifyingPoint);
        this.locationConsumer = locationConsumer;
        this.name = name;
    }

    public MapKeyPoint(@NotNull String name, @NotNull ArrayList<Location> locationList, @NotNull MapBlockIdentity originIdentifyingPoint){
        this.addIdentifyingPoint(new Vector(0,0,0), originIdentifyingPoint);
        this.locationConsumer = locationList::add;
        this.name = name;
    }

    public void addIdentifyingPoint(Vector relativePosition, MapBlockIdentity blockIdentity){
        this.identifyingPoints.put(relativePosition, blockIdentity);
    }

    public Map<Vector, MapBlockIdentity> getIdentifyingPoints() {
        return Maps.newHashMap(this.identifyingPoints);
    }

    public MapBlockIdentity getOriginIdentifyingPoint(){
        return this.identifyingPoints.get(new Vector(0,0,0));
    }
}
