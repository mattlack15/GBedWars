package me.gravitinos.bedwars.gamecore.util;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.UUID;

public class EntityStore<T extends Entity> {
    private UUID uuid = null;
    private Class<? extends Entity> type = null;
    private String world;

    private boolean isNull = false;
    public EntityStore(T entity){
        if(entity == null){
            isNull = true;
            return;
        }
        this.uuid = entity.getUniqueId();
        this.type = entity.getClass();
        this.world = entity.getWorld().getName();
    }

    public T getEntity(){
        if(isNull){
            return null;
        }

        try {
            World world = Bukkit.getWorld(this.world);

            if (world == null) {
                return (T) Bukkit.getEntity(this.uuid);
            }
            for (Entity ents : world.getEntitiesByClass(type)) {
                if (ents.getUniqueId().equals(this.uuid)) {
                    return (T) ents;
                }
            }
            return (T) Bukkit.getEntity(this.uuid);
        } catch (Exception e){
            return null;
        }

    }

    public UUID getUuid() {
        return uuid;
    }

    public Class<? extends Entity> getType(){
        return this.type;
    }
}
