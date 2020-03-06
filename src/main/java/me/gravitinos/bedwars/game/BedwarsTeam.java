package me.gravitinos.bedwars.game;

import org.bukkit.Location;

import javax.xml.ws.Provider;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Supplier;

public enum BedwarsTeam {
    RED("red", Color.RED.getRGB(), () -> SpigotBedwars.bedwarsHandler.getPointTracker().getSpawnpointsRED()),
    BLUE("blue", Color.BLUE.getRGB(), () -> SpigotBedwars.bedwarsHandler.getPointTracker().getSpawnpointsBLUE()),
    YELLOW("yellow", Color.YELLOW.getRGB(), () -> SpigotBedwars.bedwarsHandler.getPointTracker().getSpawnpointsYELLOW()),
    GREEN("green", Color.GREEN.getRGB(), () -> SpigotBedwars.bedwarsHandler.getPointTracker().getSpawnpointsGREEN());

    private final String str;
    private final int colour;
    private Supplier<ArrayList<Location>> spawnpointSupplier;

    BedwarsTeam(String string, int colour, Supplier<ArrayList<Location>> spawnpointSupplier){
        this.str = string;
        this.colour = colour;
        this.spawnpointSupplier = spawnpointSupplier;
    }

    public int getColour(){
        return this.colour;
    }

    public ArrayList<Location> getSpawnpoints(){
        return spawnpointSupplier.get();
    }

    @Override
    public String toString(){
        return str;
    }

    public static BedwarsTeam getTeam(String name){
        for(BedwarsTeam teams : values()){
            if(name.equals(teams.toString()) || name.equals(teams.name())){
                return teams;
            }
        }
        return null;
    }
}
