package me.gravitinos.minigame.diagnostic;

import me.gravitinos.minigame.SpigotMinigames;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class DiagnosticBukkitYml extends Diagnostic{

    private String message = "";

    @Override
    public boolean runDiagnostic() {
        YamlConfiguration bukkitConfig = YamlConfiguration.loadConfiguration(new File(SpigotMinigames.instance.getDataFolder().getParentFile().getParentFile(), "bukkit.yml"));

        if(bukkitConfig.isInt("ticks-per.autosave") && bukkitConfig.getInt("ticks-per.autosave") != 0){
            message = "Setting ticks-per > autosave in bukkit.yml must be set to 0. After that is done, restart the server.";
            return true;
        }

        return false;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean isFixable() {
        return false;
    }

    @Override
    public boolean fix() {
        return false;
    }

    @Override
    public void actions() {
        SpigotMinigames.instance.setRestartNeeded(true);
    }
}
