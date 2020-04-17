package me.gravitinos.minigame;

import me.gravitinos.minigame.gamecore.util.ComponentUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MinigameMessenger {
    public static void msgPlayerGame(Player player, String message, String messageType){
        message = ChatColor.translateAlternateColorCodes('&', message);
        messageType = ChatColor.translateAlternateColorCodes('&', messageType);
        player.sendMessage(ChatColor.BLUE + messageType + ChatColor.BLUE + " > " + ChatColor.GRAY + message);
    }

    public static void msgPlayerGame(Player player, TextComponent message, String messageType){
        messageType = ChatColor.translateAlternateColorCodes('&', messageType);
        TextComponent comp = new TextComponent(TextComponent.fromLegacyText(ChatColor.BLUE + messageType + ChatColor.BLUE + " > " + ChatColor.GRAY));
        comp.addExtra(message);
        player.spigot().sendMessage(comp);
    }

    public static void msgPlayerFatal(Player player, String message){
        message = ChatColor.translateAlternateColorCodes('&', message);
        msgPlayerGame(player, ChatColor.RED + message, ChatColor.DARK_RED + "" + ChatColor.BOLD + "FATAL Error");
    }

    public static void msgPlayerFatal(Player player, TextComponent message){
        TextComponent comp = new TextComponent(TextComponent.fromLegacyText(ChatColor.RED + ""));
        comp.addExtra(message);
        msgPlayerGame(player, comp, ChatColor.DARK_RED + "" + ChatColor.BOLD + "FATAL Error");
    }

    public static void msgPlayerWarning(Player player, String message){
        message = ChatColor.translateAlternateColorCodes('&', message);
        msgPlayerGame(player, ChatColor.RED + message, ChatColor.GOLD + "" + ChatColor.BOLD + "Warning");
    }

    public static void msgPlayerWarning(Player player, TextComponent message){
        TextComponent comp = new TextComponent(TextComponent.fromLegacyText(ChatColor.RED + ""));
        comp.addExtra(message);
        msgPlayerGame(player, comp, ChatColor.GOLD + "" + ChatColor.BOLD + "Warning");
    }

}
