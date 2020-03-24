package me.gravitinos.bedwars.game.module.damage;

public enum DamageType {
    PROJECTILE(DeathType.RANGED_COMBAT),
    EXPLOSION(DeathType.EXPLOSION),
    BORDER(DeathType.BORDER),
    PVP(DeathType.CLOSE_COMBAT),
    FALL(DeathType.FALLING),
    FIRE(DeathType.BURNING);


    private DeathType deathType;
    DamageType(DeathType deathType){
        this.deathType = deathType;
    }

    public DeathType getDeathType() {
        return deathType;
    }
}
