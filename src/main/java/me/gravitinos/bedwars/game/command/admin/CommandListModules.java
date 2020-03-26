package me.gravitinos.bedwars.game.command.admin;

import me.gravitinos.bedwars.game.SpigotBedwars;
import me.gravitinos.bedwars.game.command.GravCommandPermissionable;
import me.gravitinos.bedwars.game.command.GravSubCommand;
import me.gravitinos.bedwars.gamecore.module.GameModule;
import me.gravitinos.bedwars.gamecore.util.EventSubscriptions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandListModules extends GravSubCommand {
    public CommandListModules(GravCommandPermissionable parentCommand, String cmdPath) {
        super(parentCommand, cmdPath);
    }

    @Override
    public String getPermission() {
        return "bw.admin";
    }

    @Override
    public String getDescription() {
        return "Lists modules that are contained within the bedwars game handler";
    }

    @Override
    public String getAlias() {
        return "listmodules";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, Object... passedArgs) {
        if(!this.checkPermission(sender, SpigotBedwars.PLUGIN_PREFIX + "You do not have permission to use this command!")){
            return true;
        }

        if(SpigotBedwars.bedwarsHandler == null){
            return this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "Bedwars instance not found!");
        }

        this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "&e&l&nModules");
        for(GameModule module : SpigotBedwars.bedwarsHandler.getModules()){
            this.sendErrorMessage(sender, SpigotBedwars.PLUGIN_PREFIX + "&7&l" + module.getName() + " - " + (module.isEnabled() ? "&a&lENABLED" : "&c&lDISABLED") + (EventSubscriptions.instance.isSubscribed(module) ? " &7[&aT&7]" : " &7[&cF&7]"));
        }
        return true;
    }
}
