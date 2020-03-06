package me.gravitinos.bedwars.gamecore.module;

import me.gravitinos.bedwars.gamecore.handler.GameHandler;
import me.gravitinos.bedwars.gamecore.util.EventSubscriptions;
import org.jetbrains.annotations.NotNull;

public abstract class GameModule {
    private String moduleName;
    private GameHandler gameHandler;

    public GameModule(@NotNull GameHandler gameHandler, @NotNull String moduleName){
        this.moduleName = moduleName;
        this.gameHandler = gameHandler;
        EventSubscriptions.instance.subscribe(this);
    }

    public GameHandler getGameHandler(){
        return this.gameHandler;
    }

    public String getName() {
        return moduleName;
    }
}
