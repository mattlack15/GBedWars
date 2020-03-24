package me.gravitinos.bedwars.game.module.damage;

public class LastDamage {
    private String damager;
    private long time;
    private double damage;
    private DamageType damageType;
    public LastDamage(String damager, DamageType damageType, double damage, long time){
        this.damager = damager;
        this.damageType = damageType;
        this.time = time;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public double getDamage() {
        return damage;
    }

    public long getTime() {
        return time;
    }

    public String getDamager() {
        return damager;
    }
}
