package me.gravitinos.minigame.gamecore.data;

import java.util.HashMap;

public class GenericMiniPlayerGameData extends MiniPlayerGameData {
    public GenericMiniPlayerGameData(HashMap<String, String> data){
        this.setData(data);
    }

    @Override
    public String get(String key) {
        return super.get(key);
    }

    @Override
    public void set(String key, String value) {
        super.set(key, value);
    }

    @Override
    public boolean isSet(String key) {
        return super.isSet(key);
    }
}
