package me.gravitinos.bedwars.gamecore.gameitem;

import com.google.common.collect.Lists;
import me.gravitinos.bedwars.gamecore.module.GameHandler;
import me.gravitinos.bedwars.gamecore.module.GameModule;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ModuleGameItems extends GameModule {

    private ArrayList<GameItemHandler> handlers = new ArrayList<>();

    public ModuleGameItems(@NotNull GameHandler gameHandler) {
        super(gameHandler, "Game Items");
    }

    public void addGameItemHandler(GameItemHandler handler){
        this.handlers.add(handler);
        handler.enable();
    }

    public ArrayList<GameItemHandler> getGameItems(){
        return Lists.newArrayList(this.handlers);
    }

    public <T extends GameItemHandler> T getGameItem(Class<T> classType){
        for(GameItemHandler handler : handlers){
            if(classType.isAssignableFrom(handler.getClass())){
                return (T) handler;
            }
        }
        return null;
    }

    public void enable(){
        super.enable();
        this.enableAllGameItems();
    }

    public void disable(){
        super.disable();
        this.disableAllGameItems();
    }

    public GameItemHandler getGameItem(String name){
        for(GameItemHandler handler : handlers){
            if(handler.getName().equals(name)){
                return handler;
            }
        }
        return null;
    }

    public void enableAllGameItems(){
        for (GameItemHandler gameItem : this.getGameItems()) {
            if(gameItem != null){
                gameItem.enable();
            }
        }
    }

    public void disableAllGameItems(){
        for (GameItemHandler gameItem : this.getGameItems()) {
            if(gameItem != null){
                gameItem.disable();
            }
        }
    }

    public void disableGameItem(String name){
        GameItemHandler handler = this.getGameItem(name);
        if(handler != null){
            handler.disable();
        }
    }

    public void enableGameItem(String name){
        GameItemHandler handler = this.getGameItem(name);
        if(handler != null){
            handler.enable();
        }
    }

}
