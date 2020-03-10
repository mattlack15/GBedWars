package me.gravitinos.bedwars.game.info;

public enum TeamUpgrade {
    HEALING_STATION(2), HASTE(2), SHARPNESS(2), PROTECTION(2);

    private int maxLevel;

    TeamUpgrade(int maxLevel){
        this.maxLevel = maxLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }
}
