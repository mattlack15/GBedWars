package me.gravitinos.minigame.gamecore.data;

import java.util.HashMap;

public class MiniPlayerData {
    private HashMap<String, HashMap<String, String>> data;

    public MiniPlayerData(HashMap<String, HashMap<String, String>> data){
        this.data = data;
    }

    public HashMap<String, String> getData(String game){
        return this.data.get(game);
    }
}
