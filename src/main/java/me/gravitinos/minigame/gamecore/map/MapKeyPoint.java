package me.gravitinos.minigame.gamecore.map;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.SignBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class MapKeyPoint {
    private Map<Vector, MapBlockIdentity> identifyingPoints = new HashMap<>();
    private Consumer<Location> locationConsumer;
    private String name;
    public MapKeyPoint(@NotNull String name, @NotNull Consumer<Location> locationConsumer, @NotNull MapBlockIdentity originIdentifyingPoint){
        this.addIdentifyingPoint(new Vector(0,0,0), originIdentifyingPoint);
        this.locationConsumer = locationConsumer;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public MapKeyPoint(@NotNull String name, @NotNull ArrayList<Location> locationList, @NotNull MapBlockIdentity originIdentifyingPoint){
        this.addIdentifyingPoint(new Vector(0,0,0), originIdentifyingPoint);
        this.locationConsumer = locationList::add;
        this.name = name;
    }

    public void addIdentifyingPoint(Vector relativePosition, MapBlockIdentity blockIdentity){
        this.identifyingPoints.put(relativePosition, blockIdentity);
    }

    /**
     * Build this key point
     * @param session
     * @param locations
     * @throws MaxChangedBlocksException
     */
    public void build(EditSession session, ArrayList<Location> locations) throws MaxChangedBlocksException {
        for(Location locs : locations){
            for(Vector vecs : identifyingPoints.keySet()) {
                Vector vec2 = locs.toVector().add(vecs);
                MapBlockIdentity identity = identifyingPoints.get(vecs);
                BaseBlock block;
                if(identity.getId() == Material.SIGN_POST.getId() || identity.getId() == Material.WALL_SIGN.getId()){
                    block = new SignBlock(identity.getId(), identity.getData() != -1 ? identity.getData() : 0);
                } else {
                    block = new BaseBlock(identity.getId(), identity.getData() != -1 ? identity.getData() : 0);
                }
                session.setBlock(new com.sk89q.worldedit.Vector(vec2.getX(), vec2.getY(), vec2.getZ()), block);
            }
        }
    }

    /**
     * Build this key point with a specified block
     * @param session
     * @param locations
     * @param block
     * @throws MaxChangedBlocksException
     */
    public void buildWith(EditSession session, ArrayList<Location> locations, BaseBlock block) throws MaxChangedBlocksException {
        for(Location locs : locations){
            for(Vector vecs : identifyingPoints.keySet()) {
                Vector vec2 = locs.toVector().add(vecs);
                session.setBlock(new com.sk89q.worldedit.Vector(vec2.getX(), vec2.getY(), vec2.getZ()), block);
            }
        }
    }

    public Consumer<Location> getLocationConsumer(){
        return this.locationConsumer;
    }

    public Map<Vector, MapBlockIdentity> getIdentifyingPoints() {
        return this.identifyingPoints;
    }

    public MapBlockIdentity getOriginIdentifyingPoint(){
        return this.identifyingPoints.get(new Vector(0,0,0));
    }

}
