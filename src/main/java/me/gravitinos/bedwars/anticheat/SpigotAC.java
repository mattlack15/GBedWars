package me.gravitinos.bedwars.anticheat;

import me.gravitinos.bedwars.anticheat.check.Check;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import me.gravitinos.bedwars.gamecore.util.EventSubscriptions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.hamcrest.CoreMatchers;
import sun.font.CoreMetrics;

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
