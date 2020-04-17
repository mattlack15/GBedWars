package me.gravitinos.minigame.bedwars.game.command.configure;

import com.boydti.fawe.object.FawePlayer;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import me.gravitinos.minigame.bedwars.game.SpigotBedwars;
import me.gravitinos.minigame.bedwars.game.command.GravCommandPermissionable;
import me.gravitinos.minigame.bedwars.game.command.GravSubCommand;
import me.gravitinos.minigame.gamecore.CoreHandler;
import me.gravitinos.minigame.gamecore.util.ActionBar;
import me.gravitinos.minigame.gamecore.util.SyncProgressReport;
import me.gravitinos.minigame.gamecore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class CommandCreateMap extends GravSubCommand {
    public CommandCreateMap(GravCommandPermissionable parentCommand, String cmdPath) {
        super(parentCommand, cmdPath);
    }

    @Override
    public String getPermission() {
        return "bw.configure";
    }

    @Override
    public String getDescription() {
        return "Creates a map from a worldedit selection";
    }

    @Override
    public String getAlias() {
        return "createmap";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, Object... passedArgs) {
        if (!this.checkPermission(sender, SpigotBedwars.PLUGIN_PREFIX + "You do not have permission to use this command!")) {
            return true;
        }

        if (args.length < 1) {
            this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "Needs more arguments! (name of map)");
            return true;
        }

        FawePlayer player = FawePlayer.wrap(sender);

        Region selected = player.getSelection();
        if (selected == null) {
            this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "You must have a worldedit selection!");
            return true;
        }

        CuboidRegion region = (CuboidRegion) (selected);

        sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "Working...");
        SyncProgressReport<File> progress = SpigotBedwars.instance.createMap(region, args[0]);


        if(sender instanceof Player) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    ActionBar.send((Player) sender, ChatColor.YELLOW + "Map Creation - " + TextUtil.getProgressBar(progress.getPercentProgress(), 30, ":", ChatColor.GREEN + "" + ChatColor.BOLD, ChatColor.WHITE + "" + ChatColor.BOLD));
                    if(progress.getFuture().isDone()){
                        Bukkit.getScheduler().cancelTask(this.getTaskId());
                    }
                }
            }.runTaskTimer(CoreHandler.main, 0, 2);
        }

        CoreHandler.instance.getAsyncExecutor().execute(() -> {
            File file = null;
            try {
                file = progress.getFuture().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            if(file == null){
                    sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "ERROR: Map key point requirements not met!");
                }
                sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "Map created! configuration file is in the folder maps and is named &a" + file.getName());
        });
        return true;
    }
}
