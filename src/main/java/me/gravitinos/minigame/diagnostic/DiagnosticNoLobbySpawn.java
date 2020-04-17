package me.gravitinos.minigame.diagnostic;

import me.gravitinos.minigame.SpigotMinigames;
import org.bukkit.Bukkit;

public class DiagnosticNoLobbySpawn extends Diagnostic {
    private String message;

    @Override
    public boolean runDiagnostic() {

            if(!SpigotMinigames.instance.getConfig().isConfigurationSection("lobbySpawnLocation")) {
                message = "No configured lobby spawn";
            } else if(!SpigotMinigames.instance.getConfig().isDouble("lobbySpawnLocation.x") &&
                    !SpigotMinigames.instance.getConfig().isInt("lobbySpawnLocation.x")) {
                message = "No set x position for lobby spawn";
                return true;
            } else if(!SpigotMinigames.instance.getConfig().isDouble("lobbySpawnLocation.y") &&
                    !SpigotMinigames.instance.getConfig().isInt("lobbySpawnLocation.y")) {
                message = "No set y position for lobby spawn";
                return true;
            } else if(!SpigotMinigames.instance.getConfig().isDouble("lobbySpawnLocation.z") &&
                    !SpigotMinigames.instance.getConfig().isInt("lobbySpawnLocation.z")) {
                message = "No set z position for lobby spawn";
                return true;
            } else if(!SpigotMinigames.instance.getConfig().isString("lobbySpawnLocation.world") ||
            Bukkit.getWorld(SpigotMinigames.instance.getConfig().getString("lobbySpawnLocation.world")) == null){
                if(SpigotMinigames.instance.getConfig().isString("lobbySpawnLocation.world")){
                    message = "No set lobby spawn world";
                } else {
                    message = "Lobby spawn world does not exist";
                }
                return true;
            } else {
                message = "none";
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
}
