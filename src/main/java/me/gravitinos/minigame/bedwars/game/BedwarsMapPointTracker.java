package me.gravitinos.minigame.bedwars.game;

import me.gravitinos.minigame.bedwars.game.keypoints.*;
import me.gravitinos.minigame.gamecore.map.MapHandler;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BedwarsMapPointTracker {

    private Location border1 = null;
    private Location border2 = null;

    private Map<BedwarsTeam, ArrayList<Location>> spawnpoints = new HashMap<>();

    private Map<BedwarsTeam, Location> beds = new HashMap<>();

    private Map<BedwarsTeam, Location> shops = new HashMap<>();

    private ArrayList<Location> midGens = new ArrayList<>();
    private ArrayList<Location> outerGens = new ArrayList<>();
    private ArrayList<Location> baseGens = new ArrayList<>();

    public BedwarsMapPointTracker(BedwarsMapDataHandler dataHandler){
        this(dataHandler, true);
    }

    public BedwarsMapPointTracker(BedwarsMapDataHandler dataHandler, boolean load){
        if(!load){
            return;
        }

        this.setBorder1(dataHandler.getBorder1());
        this.setBorder2(dataHandler.getBorder2());

        this.setMidGens(dataHandler.getMidGeneratorLocations());
        this.setOuterGens(dataHandler.getOuterGeneratorLocations());
        this.setBaseGens(dataHandler.getBaseGeneratorLocations());

        for(BedwarsTeam teams : BedwarsTeam.values()){
            this.setBed(teams, dataHandler.getBedLocation(teams));
            this.setShop(teams, dataHandler.getShopLocation(teams));
            this.setSpawnpoints(teams, dataHandler.getSpawnpoints(teams));
        }

    }

    public Location getBorder1() {
        return border1;
    }

    public void setBorder1(Location border1) {
        this.border1 = border1;
    }

    public Location getBorder2() {
        return border2;
    }

    public void setBorder2(Location border2) {
        this.border2 = border2;
    }

    public void setSpawnpoints(BedwarsTeam team, ArrayList<Location> spawnLocations){
        this.spawnpoints.put(team, spawnLocations);
    }

    public ArrayList<Location> getSpawnpoints(BedwarsTeam team){
        this.spawnpoints.putIfAbsent(team, new ArrayList<>());
        return this.spawnpoints.get(team);
    }

    public void setBed(BedwarsTeam team, Location bedLocation){
        this.beds.put(team, bedLocation);
    }

    public void setShop(BedwarsTeam team, Location shopLocation){
        this.shops.put(team, shopLocation);
    }

    public ArrayList<Location> getMidGens() {
        return midGens;
    }

    public void setMidGens(ArrayList<Location> midGens) {
        this.midGens = midGens;
    }

    public ArrayList<Location> getOuterGens() {
        return outerGens;
    }

    public void setOuterGens(ArrayList<Location> outerGens) {
        this.outerGens = outerGens;
    }

    public ArrayList<Location> getBaseGens() {
        return baseGens;
    }

    public void setBaseGens(ArrayList<Location> baseGens) {
        this.baseGens = baseGens;
    }

    public Location getBed(BedwarsTeam team){
        return this.beds.get(team).clone();
    }

    public Location getShop(BedwarsTeam team){
        return this.shops.get(team);
    }

    //

    public void addKeyPointsToMapHandler(MapHandler handler){
        handler.clearKeyPoints();
        handler.addKeyPoint(new PointSpawnBlue((l) -> this.getSpawnpoints(BedwarsTeam.BLUE).add(l)));
        handler.addKeyPoint(new PointSpawnRed((l) -> this.getSpawnpoints(BedwarsTeam.RED).add(l)));
        handler.addKeyPoint(new PointSpawnYellow((l) -> this.getSpawnpoints(BedwarsTeam.YELLOW).add(l)));
        handler.addKeyPoint(new PointSpawnGreen((l) -> this.getSpawnpoints(BedwarsTeam.GREEN).add(l)));

        handler.addKeyPoint(new PointBorder((l) -> {
            if(this.border1 == null){
                this.border1 = l;
            } else if(border2 == null){
                this.border2 = l;
            }
        }));

        handler.addKeyPoint(new PointBaseGenerator((l) -> this.baseGens.add(l)));
        handler.addKeyPoint(new PointOuterGenerator((l) -> this.outerGens.add(l)));
        handler.addKeyPoint(new PointMidGenerator((l) -> this.midGens.add(l)));

        handler.addKeyPoint(new PointBedBlue((l) -> this.setBed(BedwarsTeam.BLUE, l)));
        handler.addKeyPoint(new PointBedRed((l) -> this.setBed(BedwarsTeam.RED, l)));
        handler.addKeyPoint(new PointBedYellow((l) -> this.setBed(BedwarsTeam.YELLOW, l)));
        handler.addKeyPoint(new PointBedGreen((l) -> this.setBed(BedwarsTeam.GREEN, l)));

        handler.addKeyPoint(new PointShopBlue((l) -> this.setShop(BedwarsTeam.BLUE, l)));
        handler.addKeyPoint(new PointShopRed((l) -> this.setShop(BedwarsTeam.RED, l)));
        handler.addKeyPoint(new PointShopYellow((l) -> this.setShop(BedwarsTeam.YELLOW, l)));
        handler.addKeyPoint(new PointShopGreen((l) -> this.setShop(BedwarsTeam.GREEN, l)));

    }

    public void export(BedwarsMapDataHandler dataHandler){

        for(BedwarsTeam teams : BedwarsTeam.values()){
            dataHandler.setBedLocation(teams, this.getBed(teams));
            dataHandler.setSpawnpoints(teams, this.getSpawnpoints(teams));
            dataHandler.setShop(teams, this.getShop(teams));
        }

        dataHandler.setBorder1(this.getBorder1());
        dataHandler.setBorder2(this.getBorder2());

        dataHandler.setMidGenerators(this.getMidGens());
        dataHandler.setOuterGenerators(this.getOuterGens());
        dataHandler.setBaseGenerators(this.getBaseGens());

    }

}
