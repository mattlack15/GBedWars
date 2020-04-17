package me.gravitinos.minigame.gamecore.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MiniPlayer {
    private static Map<UUID, MiniPlayer> playerMap = new HashMap<>();

    private Kit kit = null;
    private UUID id;
    private String name;
    private MiniPlayerData data;

    public static MiniPlayer getPlayer(UUID id, String name){
        if(playerMap.containsKey(id) && playerMap.get(id) != null){
            return playerMap.get(id);
        }

        //Make new mini player
        MiniPlayer player = new MiniPlayer(id, name, new MiniPlayerData(null)); //TODO
        playerMap.put(id, player);
        return player;
    }

    private MiniPlayer(UUID id, String name, MiniPlayerData data){
        this.id = id;
        this.name = name;
        this.data = data;
    }


    public MiniPlayerData getData(){
        return this.data;
    }
    public void setKit(Kit kit){
        this.kit = kit;
    }
    public Kit getKit(){
        return this.kit;
    }
}
