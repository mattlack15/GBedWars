package me.gravitinos.gamecore.map;

public class MapBlockIdentity {
    private int id;
    private int data;
    public MapBlockIdentity(int id, int data){
        this.id = id;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public int getData() {
        return data;
    }
}
