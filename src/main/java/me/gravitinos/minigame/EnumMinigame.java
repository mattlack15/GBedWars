package me.gravitinos.minigame;

import me.gravitinos.minigame.bedwars.game.SpigotBedwars;
import me.gravitinos.minigame.gamecore.GameServerManager;

public enum EnumMinigame {
    Bedwars(SpigotBedwars.class);

    private Class<? extends GameServerManager> manager;
    EnumMinigame(Class<? extends GameServerManager> manager){
        this.manager = manager;
    }
    public Class<? extends GameServerManager> getManagerClass(){
        return this.manager;
    }
}
