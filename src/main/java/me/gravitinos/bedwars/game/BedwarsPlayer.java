package me.gravitinos.bedwars.game;

import me.gravitinos.bedwars.game.module.playersetup.Kit;
import me.gravitinos.bedwars.game.module.playersetup.KitDefault;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BedwarsPlayer {
    private static Map<UUID, BedwarsPlayer> playerMap = new HashMap<>();

    private Kit kit = new KitDefault();
    private UUID id;

    public static BedwarsPlayer getPlayer(UUID id){
        if(playerMap.containsKey(id)){
            return playerMap.get(id);
        }

        //Make new bedwars player
        BedwarsPlayer player = new BedwarsPlayer(id);
        playerMap.put(id, player);
        return player;
    }

    private BedwarsPlayer(UUID id){
        this.id = id;
    }

    public void setKit(Kit kit){
        this.kit = kit;
    }
    public Kit getKit(){
        return this.kit;
    }
}
