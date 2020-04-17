package me.gravitinos.minigame.bedwars.game.command.admin;

import me.gravitinos.minigame.bedwars.game.BedwarsHandler;
import me.gravitinos.minigame.bedwars.game.SpigotBedwars;
import me.gravitinos.minigame.bedwars.game.command.GravCommandPermissionable;
import me.gravitinos.minigame.bedwars.game.command.GravSubCommand;
import me.gravitinos.minigame.gamecore.queue.GameQueue;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;

public class CommandStart extends GravSubCommand {
    public CommandStart(GravCommandPermissionable parentCommand, String cmdPath) {
        super(parentCommand, cmdPath);
    }

    @Override
    public String getPermission() {
        return "bw.admin";
    }

    @Override
    public String getDescription() {
        return "Starts the queue, or force starts the game";
    }

    @Override
    public String getAlias() {
        return "start";
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, Object... passedArgs) {
        if (!this.checkPermission(sender, SpigotBedwars.PLUGIN_PREFIX + "You do not have permission to use this command!")) {
            return true;
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

        if ((SpigotBedwars.bedwarsHandler != null && SpigotBedwars.bedwarsHandler.isRunning()) || (SpigotBedwars.lobbyHandler.getQueue() != null && SpigotBedwars.lobbyHandler.getQueue().isRunning())) {
            if (SpigotBedwars.lobbyHandler.getQueue() != null && SpigotBedwars.lobbyHandler.getQueue().isRunning()) {
                SpigotBedwars.lobbyHandler.endQueue();
                return sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "Game force started!");
            }
            return sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "Game or Queue is already running, stop the game first");
        }

        SpigotBedwars.lobbyHandler.setGameMap(mapFile);

        SpigotBedwars.lobbyHandler.startQueue();

        return this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "Queue started, game will start in &e" + SpigotBedwars.lobbyHandler.getQueue().getTimeLeftSeconds() + " seconds");
    }
}
