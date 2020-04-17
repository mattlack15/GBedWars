package me.gravitinos.minigame.bedwars.game.command.admin;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import me.gravitinos.minigame.bedwars.game.BedwarsMapDataHandler;
import me.gravitinos.minigame.bedwars.game.BedwarsMapPointTracker;
import me.gravitinos.minigame.bedwars.game.BedwarsTeam;
import me.gravitinos.minigame.bedwars.game.SpigotBedwars;
import me.gravitinos.minigame.bedwars.game.command.GravCommandPermissionable;
import me.gravitinos.minigame.bedwars.game.command.GravSubCommand;
import me.gravitinos.minigame.bedwars.game.keypoints.*;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;

public class CommandOpenControl extends GravSubCommand {

    public CommandOpenControl(GravCommandPermissionable parentCommand, String cmdPath) {
        super(parentCommand, cmdPath);
    }
    @Override
    public String getPermission() {
        return "bw.admin";
    }

    @Override
    public String getDescription() {
        return "Opens up a map for control point editing";
    }

    @Override
    public String getAlias() {
        return "opencontrol";
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, Object... passedArgs) {
        if (!this.checkPermission(sender, SpigotBedwars.PLUGIN_PREFIX + "You do not have permission to use this command!")) {
            return true;
        }
        
        if(!(sender instanceof Player)){
            return this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "You must be a player to use this command!");
        }

        if (args.length < 1) {
            return this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "More arguments needed, (Map)");
        }

        String map = args[0];

        File mapFile = null;
        for (File files : SpigotBedwars.instance.getMapFiles()) {
            if (files.getName().equalsIgnoreCase(map + ".yml")) {
                mapFile = files;
            }
        }

        if (mapFile == null) {
            this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "Map does not exist! These are the valid maps:");
            for (File files : SpigotBedwars.instance.getMapFiles()) {
                this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + files.getName().replace(".yml", ""));
            }
            return true;
        }

        Player p = (Player) sender;
        
        BedwarsMapDataHandler handler = new BedwarsMapDataHandler(mapFile);
        BedwarsMapPointTracker tracker = new BedwarsMapPointTracker(handler);
        
        
        EditSession session = new EditSessionBuilder(FaweAPI.getWorld(p.getWorld().getName())).allowedRegionsEverywhere().fastmode(true).build();
        try {
            //Beds
            new PointBedBlue(null).build(session, new ArrayList<Location>(){{add(tracker.getBed(BedwarsTeam.BLUE));}});
            new PointBedRed(null).build(session, new ArrayList<Location>(){{add(tracker.getBed(BedwarsTeam.RED));}});
            new PointBedYellow(null).build(session, new ArrayList<Location>(){{add(tracker.getBed(BedwarsTeam.YELLOW));}});
            new PointBedGreen(null).build(session, new ArrayList<Location>(){{add(tracker.getBed(BedwarsTeam.GREEN));}});
            
            //Generators
            new PointBaseGenerator(null).build(session, tracker.getBaseGens());
            new PointOuterGenerator(null).build(session, tracker.getOuterGens());
            new PointMidGenerator(null).build(session, tracker.getMidGens());
            
            //Spawns
            new PointSpawnBlue(null).build(session, tracker.getSpawnpoints(BedwarsTeam.BLUE));
            new PointSpawnRed(null).build(session, tracker.getSpawnpoints(BedwarsTeam.RED));
            new PointSpawnYellow(null).build(session, tracker.getSpawnpoints(BedwarsTeam.YELLOW));
            new PointSpawnGreen(null).build(session, tracker.getSpawnpoints(BedwarsTeam.GREEN));
            
            //Shops
            new PointShopBlue(null).build(session, new ArrayList<Location>(){{add(tracker.getShop(BedwarsTeam.BLUE));}});
            new PointShopRed(null).build(session, new ArrayList<Location>(){{add(tracker.getShop(BedwarsTeam.RED));}});
            new PointShopYellow(null).build(session, new ArrayList<Location>(){{add(tracker.getShop(BedwarsTeam.YELLOW));}});
            new PointShopGreen(null).build(session, new ArrayList<Location>(){{add(tracker.getShop(BedwarsTeam.GREEN));}});

            //Borders
            new PointBorder(null).build(session, new ArrayList<Location>(){{add(tracker.getBorder1()); add(tracker.getBorder2());}});

            session.flushQueue();

        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
            this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "Problem");
        }
        return this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "Successfully built the control points for &e" + map);
    }
}
