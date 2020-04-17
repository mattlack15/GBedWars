package me.gravitinos.minigame.gamecore.map;

import org.bukkit.Material;

public class MapBlockIdentity {
    private int id;
    private int data;
    private Object[] extraData;

    /**
     * Constructor
     * @param id The id
     * @param data The data, -1 is wildcard
     * @param extraData Usually used for Sign Text content
     */
    public MapBlockIdentity(int id, int data, Object... extraData){
        this.id = id;
        this.data = data;
        this.extraData = extraData;
    }
    public MapBlockIdentity(Material material, int data, Object... extraData){
        this(material.getId(), data, extraData);
    }

    public Object[] getExtraData() {
        return extraData;
    }

    public int getId() {
        return id;
    }

    public int getData() {
        return data;
    }
}
