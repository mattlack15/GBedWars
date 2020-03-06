package me.gravitinos.bedwars.game.command.admin;

import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.game.BedwarsTeam;
import me.gravitinos.bedwars.game.SpigotBedwars;
import me.gravitinos.bedwars.game.command.GravCommandPermissionable;
import me.gravitinos.bedwars.game.command.GravSubCommand;
import me.gravitinos.bedwars.gamecore.handler.GameHandler;
import me.gravitinos.bedwars.gamecore.queue.GameQueue;
import me.gravitinos.bedwars.gamecore.util.WeakList;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.lang.ref.WeakReference;

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
        if(!this.checkPermission(sender, SpigotBedwars.PLUGIN_PREFIX + "You do not have permission to use this command!")){
            return true;
        }

        if(args.length  < 1){
            return this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "More arguments needed, (Map)");
        }

        String map = args[0];

        File mapFile = null;
        for(File files : SpigotBedwars.instance.getMapFiles()){
            if(files.getName().equalsIgnoreCase(map + ".yml")){
                mapFile = files;
            }
        }

        if(mapFile == null){
            this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "Map does not exist! These are the valid maps:");
            for(File files : SpigotBedwars.instance.getMapFiles()){
                this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + files.getName().replace(".yml", ""));
            }
            return true;
        }

        if((SpigotBedwars.bedwarsHandler != null && SpigotBedwars.bedwarsHandler.isRunning()) || (SpigotBedwars.queue != null && SpigotBedwars.queue.isRunning())){
            return sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "Game or Queue is already running, stop the game first");
        }

        SpigotBedwars.bedwarsHandler = new BedwarsHandler(mapFile);
        GameQueue queue = new GameQueue(SpigotBedwars.bedwarsHandler, 10);
        queue.setShowActionBar(true);
        queue.setActionBarMessage("&cBed wars starting in &f<timeLeftSeconds> seconds &7- &e<numQueued>/<maxQueued>");
        queue.start();
        Bukkit.getOnlinePlayers().forEach(p -> {
            queue.queuePlayer(p.getUniqueId());
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 10f, 1f);
        });

        SpigotBedwars.queue = queue;

        return this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "Queue started, game will start in &e" + queue.getTimeLeftSeconds() + " seconds");
    }
}
