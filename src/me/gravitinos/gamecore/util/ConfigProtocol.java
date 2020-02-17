package me.gravitinos.gamecore.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigProtocol {
    /**
     * Load a location from config
     * @param section Configuration section
     * @return Location
     */
    public static Location loadLocation(ConfigurationSection section){
        if(section == null || !section.isSet("x") || !section.isSet("y") || !section.isSet("z") || !section.isSet("world")){
            return null;
        }
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        String world = section.getString("world");
        World w = Bukkit.getWorld(world);
        if(w == null){
            w = Bukkit.getWorlds().get(0);
        }

        return new Location(w,x,y,z);

    }

    /**
     * Save a location to config
     * @param section Configuration section
     * @param loc Location
     */
    public static void saveLocation(ConfigurationSection section, Location loc){
        if(section == null){
            return;
        }
        section.set("x", loc.getX());
        section.set("y", loc.getY());
        section.set("z", loc.getZ());
        section.set("world", loc.getWorld().getName());
    }
}
