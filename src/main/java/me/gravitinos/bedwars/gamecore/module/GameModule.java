package me.gravitinos.bedwars.gamecore.module;

import me.gravitinos.bedwars.gamecore.util.EventSubscriptions;
import org.jetbrains.annotations.NotNull;

public abstract class GameModule {
    private String moduleName;
    private GameHandler gameHandler;
    private boolean enabled = false;

    public GameModule(GameHandler gameHandler, @NotNull String moduleName){
        this.moduleName = moduleName;
        this.gameHandler = gameHandler;
    }

    public void enable(){
        this.enabled = true;
        EventSubscriptions.instance.subscribe(this);
    }

    public void disable(){
        this.enabled = false;
        EventSubscriptions.instance.unSubscribe(this);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public GameHandler getGameHandler(){
        return this.gameHandler;
    }

    public String getName() {
        return moduleName;
    }

    protected void setGameHandler(GameHandler handler){
        this.gameHandler = handler;
    }
}
