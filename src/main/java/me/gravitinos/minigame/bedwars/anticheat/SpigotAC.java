package me.gravitinos.minigame.bedwars.anticheat;

import me.gravitinos.minigame.bedwars.anticheat.check.Check;
import me.gravitinos.minigame.gamecore.CoreHandler;
import me.gravitinos.minigame.gamecore.util.EventSubscription;
import me.gravitinos.minigame.gamecore.util.EventSubscriptions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

public class SpigotAC {
    public static SpigotAC instance;
    public SpigotAC(){
        instance = this;
        EventSubscriptions.instance.subscribe(this);
        for(Player players : Bukkit.getOnlinePlayers()){
            Check.createProfile(players);
        }
    }

    public static void consoleLog(String message){
        message = ChatColor.translateAlternateColorCodes('&', message);
        Bukkit.getLogger().info(message); //Possibly add prefix later
    }

    @EventSubscription
    private void onJoin(PlayerJoinEvent event) {
        Check.createProfile(event.getPlayer());
    }

    @EventSubscription
    private void onQuit(PlayerQuitEvent event) {
        Check.removeProfile(event.getPlayer().getUniqueId());
    }

    @EventSubscription
    private void onDisable(PluginDisableEvent event){
        if(event.getPlugin().equals(CoreHandler.main)){
            Check.clearProfiles();
        }
    }

}
