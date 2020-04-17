package me.gravitinos.minigame.bedwars.game.module.damage;

import java.util.ArrayList;

public class LastDamageList {
    private ArrayList<LastDamage> lastDamages = new ArrayList<>();

    public void addLastDamage(LastDamage ld){
        this.lastDamages.add(0, ld);
    }

    public ArrayList<LastDamage> getLastDamages(){
        return this.lastDamages;
    }

    public void clear(){
        this.lastDamages.clear();
    }

    public void removeDamagesPast(int millisecondsSinceDamage){
        lastDamages.removeIf(ld -> System.currentTimeMillis() - ld.getTime() > millisecondsSinceDamage);
    }
}
