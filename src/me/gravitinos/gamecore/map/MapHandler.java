package me.gravitinos.gamecore.map;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.ArrayList;

public class MapHandler {
    private boolean mapPrePlaced = true;
    private File mapFile = null;
    private ArrayList<MapKeyPoint> keyPoints = new ArrayList<>();

    public MapHandler(){}

    public MapHandler(File mapSchematic){
        this.mapFile = mapSchematic;
    }

    public void addKeyPoint(MapKeyPoint point){
        this.keyPoints.add(point);
    }

    public void removeKeyPoint(MapKeyPoint point){
        this.keyPoints.remove(point);
    }

    public ArrayList<MapKeyPoint> getKeyPoints() {
        return Lists.newArrayList(this.keyPoints);
    }


}
