package me.gravitinos.minigame.bedwars.game.command.admin;

import me.gravitinos.minigame.bedwars.game.SpigotBedwars;
import me.gravitinos.minigame.bedwars.game.command.GravCommandPermissionable;
import me.gravitinos.minigame.bedwars.game.command.GravSubCommand;
import me.gravitinos.minigame.gamecore.module.GameStopReason;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandStop extends GravSubCommand {
    public CommandStop(GravCommandPermissionable parentCommand, String cmdPath) {
        super(parentCommand, cmdPath);
    }

    @Override
    public String getPermission() {
        return "bw.admin";
    }

    @Override
    public String getDescription() {
        return "Stops the game, or queue if one is running";
    }

    @Override
    public String getAlias() {
        return "stop";
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, Object... passedArgs) {
        if (!this.checkPermission(sender, SpigotBedwars.PLUGIN_PREFIX + "You do not have permission to use this command!")) {
            return true;
        }

        String reason = "Staff Discretion";
        StringBuilder reasonBuilder = new StringBuilder();
        for(int i = 0; i < args.length; i++){
            reasonBuilder.append(args[i]).append(" ");
        }
        reason = reasonBuilder.length() != 0 ? reasonBuilder.toString() : reason;

        if (SpigotBedwars.queue != null && SpigotBedwars.queue.isRunning()) {
            SpigotBedwars.queue.pause();
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(SpigotBedwars.PLUGIN_PREFIX + "Queue has been halted, " + reason);
            }
            return this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "Queue halted!");
        }
        if(SpigotBedwars.lobbyHandler.getGame() != null && SpigotBedwars.lobbyHandler.getGame().isRunning()){
            SpigotBedwars.lobbyHandler.getGame().stop(reason, GameStopReason.STAFF_DISCRETION);
            return this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "Game halted!");
        }

        return this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "Neither a game nor queue are running!");
    }
}
