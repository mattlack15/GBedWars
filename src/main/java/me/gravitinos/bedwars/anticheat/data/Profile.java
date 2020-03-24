package me.gravitinos.bedwars.anticheat.data;

import me.gravitinos.bedwars.anticheat.check.Check;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class Profile {

    private UUID uniqueId;
    private String name;
    private ArrayList<Check> checks = new ArrayList<>();

    public Profile(Player player){
        this.uniqueId = player.getUniqueId();
        this.name = player.getName();
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(uniqueId);
    }

    public String getName() {
        return name;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public ArrayList<Check> getChecks(){
        return this.checks;
    }

    public void addCheck(Check check){
        this.checks.add(check);
    }

    public <T> T getCheck(Class<T> clazz){
        for(Check checks : this.checks){
            if(clazz.isAssignableFrom(checks.getClass())){
                return (T) checks;
            }
        }
        return null;
    }
}
