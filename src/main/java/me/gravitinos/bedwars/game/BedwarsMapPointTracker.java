package me.gravitinos.bedwars.game;

import me.gravitinos.bedwars.game.keypoints.*;
import me.gravitinos.bedwars.gamecore.map.MapHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;

public class BedwarsMapPointTracker {

    private Location border1 = null;
    private Location border2 = null;

    private ArrayList<Location> spawnpointsBLUE = new ArrayList<>();
    private ArrayList<Location> spawnpointsRED = new ArrayList<>();
    private ArrayList<Location> spawnpointsYELLOW = new ArrayList<>();
    private ArrayList<Location> spawnpointsGREEN = new ArrayList<>();

    private Location bedRED = null;
    private Location bedBLUE = null;
    private Location bedYELLOW = null;
    private Location bedGREEN = null;

    private Location shopRED = null;
    private Location shopBLUE = null;
    private Location shopYELLOW = null;
    private Location shopGREEN = null;

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
        this.setBedBLUE(dataHandler.getBedLocation(BedwarsTeam.BLUE));
        this.setBedRED(dataHandler.getBedLocation(BedwarsTeam.RED));
        this.setBedYELLOW(dataHandler.getBedLocation(BedwarsTeam.YELLOW));
        this.setBedGREEN(dataHandler.getBedLocation(BedwarsTeam.GREEN));

        this.setBorder1(dataHandler.getBorder1());
        this.setBorder2(dataHandler.getBorder2());

        this.setMidGens(dataHandler.getMidGeneratorLocations());
        this.setOuterGens(dataHandler.getOuterGeneratorLocations());
        this.setBaseGens(dataHandler.getBaseGeneratorLocations());

        this.setShopBLUE(dataHandler.getShopLocation(BedwarsTeam.BLUE));
        this.setShopRED(dataHandler.getShopLocation(BedwarsTeam.RED));
        this.setShopYELLOW(dataHandler.getShopLocation(BedwarsTeam.YELLOW));
        this.setShopGREEN(dataHandler.getShopLocation(BedwarsTeam.GREEN));

        this.setSpawnpointsBLUE(dataHandler.getSpawnpoints(BedwarsTeam.BLUE));
        this.setSpawnpointsRED(dataHandler.getSpawnpoints(BedwarsTeam.RED));
        this.setSpawnpointsYELLOW(dataHandler.getSpawnpoints(BedwarsTeam.YELLOW));
        this.setSpawnpointsGREEN(dataHandler.getSpawnpoints(BedwarsTeam.GREEN));
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

    public ArrayList<Location> getSpawnpointsBLUE() {
        return spawnpointsBLUE;
    }

    public void setSpawnpointsBLUE(ArrayList<Location> spawnpointsBLUE) {
        this.spawnpointsBLUE = spawnpointsBLUE;
    }

    public ArrayList<Location> getSpawnpointsRED() {
        return spawnpointsRED;
    }

    public void setSpawnpointsRED(ArrayList<Location> spawnpointsRED) {
        this.spawnpointsRED = spawnpointsRED;
    }

    public ArrayList<Location> getSpawnpointsYELLOW() {
        return spawnpointsYELLOW;
    }

    public void setSpawnpointsYELLOW(ArrayList<Location> spawnpointsYELLOW) {
        this.spawnpointsYELLOW = spawnpointsYELLOW;
    }

    public ArrayList<Location> getSpawnpointsGREEN() {
        return spawnpointsGREEN;
    }

    public void setSpawnpointsGREEN(ArrayList<Location> spawnpointsGREEN) {
        this.spawnpointsGREEN = spawnpointsGREEN;
    }

    public Location getBedRED() {
        return bedRED;
    }

    public void setBedRED(Location bedRED) {
        this.bedRED = bedRED;
    }

    public Location getBedBLUE() {
        return bedBLUE;
    }

    public void setBedBLUE(Location bedBLUE) {
        this.bedBLUE = bedBLUE;
    }

    public Location getBedYELLOW() {
        return bedYELLOW;
    }

    public void setBedYELLOW(Location bedYELLOW) {
        this.bedYELLOW = bedYELLOW;
    }

    public Location getBedGREEN() {
        return bedGREEN;
    }

    public void setBedGREEN(Location bedGREEN) {
        this.bedGREEN = bedGREEN;
    }

    public Location getShopRED() {
        return shopRED;
    }

    public void setShopRED(Location shopRED) {
        this.shopRED = shopRED;
    }

    public Location getShopBLUE() {
        return shopBLUE;
    }

    public void setShopBLUE(Location shopBLUE) {
        this.shopBLUE = shopBLUE;
    }

    public Location getShopYELLOW() {
        return shopYELLOW;
    }

    public void setShopYELLOW(Location shopYELLOW) {
        this.shopYELLOW = shopYELLOW;
    }

    public Location getShopGREEN() {
        return shopGREEN;
    }

    public void setShopGREEN(Location shopGREEN) {
        this.shopGREEN = shopGREEN;
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

    //Extras

    public ArrayList<Location> getSpawnpoints(BedwarsTeam team){
        switch(team){
            case BLUE:
                return this.getSpawnpointsBLUE();
            case YELLOW:
                return this.getSpawnpointsYELLOW();
            case GREEN:
                return this.getSpawnpointsGREEN();
            case RED:
                return this.getSpawnpointsRED();

            default:
                return null;
        }
    }

    public Location getBed(BedwarsTeam team){
        switch(team){
            case BLUE:
                return this.getBedBLUE();
            case YELLOW:
                return this.getBedYELLOW();
            case GREEN:
                return this.getBedGREEN();
            case RED:
                return this.getBedRED();

            default:
                return null;
        }
    }

    public Location getShop(BedwarsTeam team){
        switch(team){
            case BLUE:
                return this.getShopBLUE();
            case YELLOW:
                return this.getShopYELLOW();
            case GREEN:
                return this.getShopGREEN();
            case RED:
                return this.getShopRED();

            default:
                return null;
        }
    }

    //


    public void addKeyPointsToMapHandler(MapHandler handler){
        handler.clearKeyPoints();
        handler.addKeyPoint(new PointSpawnBlue((l) -> this.spawnpointsBLUE.add(l)));
        handler.addKeyPoint(new PointSpawnRed((l) -> this.spawnpointsRED.add(l)));
        handler.addKeyPoint(new PointSpawnYellow((l) -> this.spawnpointsYELLOW.add(l)));
        handler.addKeyPoint(new PointSpawnGreen((l) -> this.spawnpointsGREEN.add(l)));

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

        handler.addKeyPoint(new PointBedBlue((l) -> this.bedBLUE = l));
        handler.addKeyPoint(new PointBedRed((l) -> this.bedRED = l));
        handler.addKeyPoint(new PointBedYellow((l) -> this.bedYELLOW = l));
        handler.addKeyPoint(new PointBedGreen((l) -> this.bedGREEN = l));

        handler.addKeyPoint(new PointShopBlue((l) -> this.shopBLUE = l));
        handler.addKeyPoint(new PointShopRed((l) -> this.shopRED = l));
        handler.addKeyPoint(new PointShopYellow((l) -> this.shopYELLOW = l));
        handler.addKeyPoint(new PointShopGreen((l) -> this.shopGREEN = l));

    }

    public void export(BedwarsMapDataHandler dataHandler){

        dataHandler.setBedLocation(BedwarsTeam.BLUE, this.getBedBLUE());
        dataHandler.setBedLocation(BedwarsTeam.RED, this.getBedRED());
        dataHandler.setBedLocation(BedwarsTeam.YELLOW, this.getBedYELLOW());
        dataHandler.setBedLocation(BedwarsTeam.GREEN, this.getBedGREEN());

        dataHandler.setBorder1(this.getBorder1());
        dataHandler.setBorder2(this.getBorder2());

        dataHandler.setMidGenerators(this.getMidGens());
        dataHandler.setOuterGenerators(this.getOuterGens());
        dataHandler.setBaseGenerators(this.getBaseGens());

        dataHandler.setShop(BedwarsTeam.BLUE, this.getShopBLUE());
        dataHandler.setShop(BedwarsTeam.RED, this.getShopRED());
        dataHandler.setShop(BedwarsTeam.YELLOW, this.getShopYELLOW());
        dataHandler.setShop(BedwarsTeam.GREEN, this.getShopGREEN());

        dataHandler.setSpawnpoints(BedwarsTeam.BLUE, this.getSpawnpointsBLUE());
        dataHandler.setSpawnpoints(BedwarsTeam.RED, this.getSpawnpointsRED());
        dataHandler.setSpawnpoints(BedwarsTeam.YELLOW, this.getSpawnpointsYELLOW());
        dataHandler.setSpawnpoints(BedwarsTeam.GREEN, this.getSpawnpointsGREEN());

    }

}
