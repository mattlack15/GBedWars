package me.gravitinos.bedwars.game;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import javax.xml.ws.Provider;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Supplier;

public enum BedwarsTeam {
    RED("Red", 0xFF0000, ChatColor.RED, (byte) 14),
    BLUE("Blue", 0x0088FF, ChatColor.AQUA, (byte) 11),
    YELLOW("Yellow", 0xFADD00, ChatColor.YELLOW, (byte) 4),
    GREEN("Green", 0x00FF00, ChatColor.GREEN, (byte) 5);

    private final String str;
    private final int colour;
    private final ChatColor chatColor;
    private final byte woolColor;

    BedwarsTeam(String string, int colour, ChatColor chatColor, byte woolColor){
        this.str = string;
        this.chatColor = chatColor;
        this.woolColor = woolColor;
        this.colour = colour;
    }

    public String getName(){
        return this.str;
    }

    public int getColour(){
        return this.colour;
    }

    public byte getWoolColour() {
        return woolColor;
    }

    public ChatColor getChatColour() {
        return chatColor;
    }

    @Override
    public String toString(){
        return getName();
    }

    public static BedwarsTeam getTeam(String name){
        if(name == null){
            return null;
        }
        for(BedwarsTeam teams : values()){
            if(name.equals(teams.toString()) || name.equals(teams.name())){
                return teams;
            }
        }
        return null;
    }
}
