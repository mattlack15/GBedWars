package me.gravitinos.minigame.gamecore.data;

import com.google.common.collect.Maps;

import java.util.HashMap;

public abstract class MiniPlayerGameData {

    private HashMap<String, String> data = new HashMap<>();

    /**
     * Base constructor
     */
    public MiniPlayerGameData(){ }

    /**
     * constructor for modifying data from generic game data object
     */
    public MiniPlayerGameData(MiniPlayerGameData data){
        this.setData(data.data);
    }

    protected String get(String key){
        return this.data.get(key);
    }

    protected void set(String key, String value){
        this.data.put(key, value);
    }

    protected boolean isSet(String key){
        return get(key) != null;
    }

    protected void setData(HashMap<String, String> data){
        this.data = data;
    }
}
