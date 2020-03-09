package me.gravitinos.bedwars.game;

import org.bukkit.Location;

import javax.xml.ws.Provider;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Supplier;

public enum BedwarsTeam {
    RED("red", 0xFF0000, () -> SpigotBedwars.bedwarsHandler.getPointTracker().getSpawnpointsRED()),
    BLUE("blue", 0x0000FF, () -> SpigotBedwars.bedwarsHandler.getPointTracker().getSpawnpointsBLUE()),
    YELLOW("yellow", 0xFADD00, () -> SpigotBedwars.bedwarsHandler.getPointTracker().getSpawnpointsYELLOW()),
    GREEN("green", 0x00FF00, () -> SpigotBedwars.bedwarsHandler.getPointTracker().getSpawnpointsGREEN());

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
        if(name == null){
            return null;
        }
        for(BedwarsTeam teams : values()){
            if(name.equals(teams.toString()) || name.equals(teams.name())){
                return teams;
            }
        }
        return null;
    }
}
