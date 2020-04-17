package me.gravitinos.minigame.bedwars.game.module.damage;

public enum DeathType {
    KNOCKED_OFF("knocked off the map"),
    BORDER("vaporized"),
    EXPLOSION("blown up"),
    RANGED_COMBAT("shot"),
    CLOSE_COMBAT("killed"),
    BURNING("burned alive"),
    FALLING("died by falling");

    private String deathMessage;
    DeathType(String deathMessage){
        this.deathMessage = deathMessage;
    }

    public String getDeathMessage() {
        return deathMessage;
    }
}
