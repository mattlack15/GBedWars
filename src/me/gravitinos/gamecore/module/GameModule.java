package me.gravitinos.gamecore.module;

import me.gravitinos.gamecore.handler.GameHandler;
import me.gravitinos.gamecore.util.EventSubscription;
import me.gravitinos.gamecore.util.EventSubscriptions;
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
