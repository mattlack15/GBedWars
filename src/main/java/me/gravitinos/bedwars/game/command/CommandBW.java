package me.gravitinos.bedwars.game.command;

import me.gravitinos.bedwars.game.SpigotBedwars;
import me.gravitinos.bedwars.game.command.admin.CommandStart;
import me.gravitinos.bedwars.game.command.configure.CommandCreateMap;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class CommandBW extends GravCommand {

    public CommandBW(){
        this.addSubCommand(new CommandStart(this, this.getSubCommandCmdPath()));
        this.addSubCommand(new CommandCreateMap(this, this.getSubCommandCmdPath()));
    }

    @Override
    public String getDescription() {
        return "Main command for bedwars";
    }

    @Override
    public ArrayList<String> getAliases() {
        return new ArrayList<String>() {{ add("bw"); }};
    }

    @Override
    public String getPermission() {
        return "bw.use";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!this.checkPermission(sender, "Unknown command. Type \"/help\" for help.")){
            return true;
        }

        if(args.length < 1){
            ArrayList<String> help = this.getEndingHelpMessages(GravCommand.DEFAULT_HELP_FORMAT, 0);
            help.forEach(s1 -> this.sendErrorMessage(sender, s1));
            return true;
        }
        GravSubCommand subCommand = this.getSubCommand(args[0]);
        if(subCommand != null){
            this.callSubCommand(subCommand, sender, cmd, label, args);
            return true;
        }
        this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "Sub command not recognized!");
        return true;

    }
}
