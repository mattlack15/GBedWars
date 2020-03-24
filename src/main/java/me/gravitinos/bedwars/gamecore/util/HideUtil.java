package me.gravitinos.bedwars.gamecore.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HideUtil {
    private static HideUtil instance;
    private static List<UUID> hidden = new ArrayList<>();

    public HideUtil(){
        EventSubscriptions.instance.subscribe(this);
    }

    @EventSubscription
    public void onLeave(PlayerQuitEvent e){
        HideUtil.unHidePlayer(e.getPlayer());
    }

    @OnDisable
    public void onDisable(){
        List<UUID> hidden1 = ListUtils.clone(hidden);
        for (UUID uuid : hidden1) {
            Player p = Bukkit.getPlayer(uuid);
            if(p != null){
                unHidePlayer(p);
            }
        }
    }

    static {
        instance = new HideUtil();
    }

    public static void hidePlayer(@NotNull Player p, String exceptionPermission){
        Bukkit.getOnlinePlayers().stream()
                .filter(ps -> !ps.hasPermission(exceptionPermission))
                .forEach(ps -> ps.hidePlayer(p));
        HideUtil.hidden.add(p.getUniqueId());
    }

    public static boolean isHidden(@NotNull UUID player){
        return hidden.contains(player);
    }

    public static void hidePlayer(@NotNull Player p){
        Bukkit.getOnlinePlayers().forEach(ps -> ps.hidePlayer(p));
        HideUtil.hidden.add(p.getUniqueId());
    }

    public static void unHidePlayer(@NotNull Player p){
        Bukkit.getOnlinePlayers().forEach(ps -> ps.showPlayer(p));
        HideUtil.hidden.remove(p.getUniqueId());
    }
}
