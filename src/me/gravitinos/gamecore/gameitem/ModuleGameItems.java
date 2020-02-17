package me.gravitinos.gamecore.gameitem;

import com.google.common.collect.Lists;
import me.gravitinos.gamecore.handler.GameHandler;
import me.gravitinos.gamecore.module.GameModule;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ModuleGameItems extends GameModule {

    private ArrayList<GameItemHandler> handlers = new ArrayList<>();

    public ModuleGameItems(@NotNull GameHandler gameHandler) {
        super(gameHandler, "Game items");
    }

    public void addGameItemHandler(GameItemHandler handler){
        this.handlers.add(handler);
        handler.enable();
    }

    public ArrayList<GameItemHandler> getGameItems(){
        return Lists.newArrayList(this.handlers);
    }

    public GameItemHandler getGameItem(String name){
        for(GameItemHandler handler : handlers){
            if(handler.getName().equals(name)){
                return handler;
            }
        }
        return null;
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