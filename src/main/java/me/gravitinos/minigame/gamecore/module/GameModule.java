package me.gravitinos.minigame.gamecore.module;

import me.gravitinos.minigame.gamecore.util.EventSubscriptions;
import org.jetbrains.annotations.NotNull;

public abstract class GameModule {
    private String moduleName;
    private GameHandler gameHandler = null;
    private boolean enabled = false;

    public GameModule(GameHandler handler, @NotNull String moduleName){
        this.moduleName = moduleName;
        this.gameHandler = handler;
    }
    public GameModule(@NotNull String moduleName){
        this(null, moduleName);
    }

    public void enable(){
        this.enabled = true;
        EventSubscriptions.instance.subscribe(this);
      //  this.onEnable();
    }

    public void disable(){
        this.enabled = false;
        EventSubscriptions.instance.unSubscribe(this);
       // this.onDisable();
    }

    //public abstract void onEnable();
   // public abstract void onDisable(); TODO Maybe add this in next minigame or update of gamecore

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
