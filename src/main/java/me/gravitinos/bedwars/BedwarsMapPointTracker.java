package me.gravitinos.bedwars;

import me.gravitinos.bedwars.keypoints.*;
import me.gravitinos.gamecore.map.MapHandler;
import org.bukkit.Location;

import java.util.ArrayList;

import static me.gravitinos.gamecore.CoreHandler.main;

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

    public void addKeyPointsToMapHandler(MapHandler handler){
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

        handler.addKeyPoint(new PointBedBlue((l) -> this.bedBLUE = l));
        handler.addKeyPoint(new PointBedRed((l) -> this.bedRED = l));
        handler.addKeyPoint(new PointBedYellow((l) -> this.bedYELLOW = l));
        handler.addKeyPoint(new PointBedGreen((l) -> this.bedGREEN = l));

        handler.addKeyPoint(new PointShopBlue((l) -> this.bedBLUE = l));
        handler.addKeyPoint(new PointShopRed((l) -> this.bedRED = l));
        handler.addKeyPoint(new PointShopYellow((l) -> this.bedYELLOW = l));
        handler.addKeyPoint(new PointShopGreen((l) -> this.bedGREEN = l));

    }

}
