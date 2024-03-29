package me.gravitinos.minigame.bedwars.game;

import me.gravitinos.minigame.gamecore.util.ConfigProtocol;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BedwarsMapDataHandler {
    public static BedwarsMapDataHandler instance;

    private static final String BORDER1 = "border1";
    private static final String BORDER2 = "border2";
    private static final String SPAWNPOINTS = "spawnpoints";
    private static final String BED = "bed_location";
    private static final String MID_GENERATORS = "mid_generators";
    private static final String OUTER_GENERATORS = "outer_generators";
    private static final String BASE_GENERATORS = "base_generators";
    private static final String SHOP = "shop_location";

    private File file;

    private FileConfiguration config;

    public BedwarsMapDataHandler(File file) {
        instance = this;
        this.file = file;
        config = YamlConfiguration.loadConfiguration(file);
        this.setupConfig();
    }

    private void setupConfig() {
        createSectionIfNotExists(BedwarsTeam.BLUE + "." + SPAWNPOINTS);
        createSectionIfNotExists(BedwarsTeam.RED + "." + SPAWNPOINTS);
        createSectionIfNotExists(BedwarsTeam.YELLOW + "." + SPAWNPOINTS);
        createSectionIfNotExists(BedwarsTeam.GREEN + "." + SPAWNPOINTS);
        createSectionIfNotExists(MID_GENERATORS);
        createSectionIfNotExists(OUTER_GENERATORS);
        createSectionIfNotExists(BASE_GENERATORS);
        saveConfig();
    }

    private void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ConfigurationSection createSectionIfNotExists(String path) {
        if (!config.isConfigurationSection(path)) {
            ConfigurationSection section = config.createSection(path);
            this.saveConfig();
            return section;
        } else {
            return config.getConfigurationSection(path);
        }
    }

    public Location getBorder1() {
        return ConfigProtocol.loadLocation(config.getConfigurationSection(BORDER1));
    }

    public Location getBorder2() {
        return ConfigProtocol.loadLocation(config.getConfigurationSection(BORDER2));
    }

    public ArrayList<Location> getSpawnpoints(BedwarsTeam team) {
        ArrayList<Location> locs = new ArrayList<>();
        for (String keys : createSectionIfNotExists(team + "." + SPAWNPOINTS).getKeys(false)) {
            Location loc = ConfigProtocol.loadLocation(config.getConfigurationSection(team + "." + SPAWNPOINTS + "." + keys));
            if (loc != null) {
                locs.add(loc);
            }
        }
        return locs;
    }

    public Location getBedLocation(BedwarsTeam team) {
        return ConfigProtocol.loadLocation(config.getConfigurationSection(team + "." + BED));
    }

    public Location getShopLocation(BedwarsTeam team) {
        return ConfigProtocol.loadLocation(config.getConfigurationSection(team + "." + SHOP));
    }

    public ArrayList<Location> getMidGeneratorLocations() {
        ArrayList<Location> locs = new ArrayList<>();
        for (String keys : createSectionIfNotExists(MID_GENERATORS).getKeys(false)) {
            Location loc = ConfigProtocol.loadLocation(config.getConfigurationSection(MID_GENERATORS + "." + keys));
            if (loc != null) {
                locs.add(loc);
            }
        }
        return locs;
    }

    public ArrayList<Location> getOuterGeneratorLocations() {
        ArrayList<Location> locs = new ArrayList<>();
        for (String keys : createSectionIfNotExists(OUTER_GENERATORS).getKeys(false)) {
            Location loc = ConfigProtocol.loadLocation(config.getConfigurationSection(OUTER_GENERATORS + "." + keys));
            if (loc != null) {
                locs.add(loc);
            }
        }
        return locs;
    }

    public ArrayList<Location> getBaseGeneratorLocations() {
        ArrayList<Location> locs = new ArrayList<>();
        for (String keys : createSectionIfNotExists(BASE_GENERATORS).getKeys(false)) {
            Location loc = ConfigProtocol.loadLocation(config.getConfigurationSection(BASE_GENERATORS + "." + keys));
            if (loc != null) {
                locs.add(loc);
            }
        }
        return locs;
    }

    public void setBorder1(Location loc) {
        ConfigProtocol.saveLocation(createSectionIfNotExists(BORDER1), loc);
        saveConfig();
    }

    public void setBorder2(Location loc) {
        ConfigProtocol.saveLocation(createSectionIfNotExists(BORDER2), loc);
        saveConfig();
    }

    public void setSpawnpoints(BedwarsTeam team, ArrayList<Location> locs) {
        ConfigurationSection teamSection = createSectionIfNotExists(team + "");
        if (teamSection != null) {
            for (int i = 0; i < locs.size(); i++) {
                ConfigProtocol.saveLocation(createSectionIfNotExists(team + "." + SPAWNPOINTS + "." + i), locs.get(i));
            }
        }
        saveConfig();
    }

    public void setBedLocation(BedwarsTeam team, Location loc) {
        ConfigProtocol.saveLocation(createSectionIfNotExists(team + "." + BED), loc);
        saveConfig();
    }

    public void setOuterGenerators(ArrayList<Location> locs) {
        for (int i = 0; i < locs.size(); i++) {
            ConfigProtocol.saveLocation(createSectionIfNotExists(OUTER_GENERATORS + "." + i), locs.get(i));
        }
        saveConfig();
    }

    public void setMidGenerators(ArrayList<Location> locs) {
        for (int i = 0; i < locs.size(); i++) {
            ConfigProtocol.saveLocation(createSectionIfNotExists(MID_GENERATORS + "." + i), locs.get(i));
        }
        saveConfig();
    }
    public void setBaseGenerators(ArrayList<Location> locs) {
        for (int i = 0; i < locs.size(); i++) {
            ConfigProtocol.saveLocation(createSectionIfNotExists(BASE_GENERATORS + "." + i), locs.get(i));
        }
        saveConfig();
    }

    public void setShop(BedwarsTeam team, Location loc) {
        ConfigProtocol.saveLocation(createSectionIfNotExists(team + "." + SHOP), loc);
        saveConfig();
    }
}
