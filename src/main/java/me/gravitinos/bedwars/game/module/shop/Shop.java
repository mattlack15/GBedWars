package me.gravitinos.bedwars.game.module.shop;

import me.gravitinos.bedwars.gamecore.util.EntityStore;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class Shop {
    private Location location;
    private EntityStore<Villager> villager = new EntityStore<>(null);
    private ShopInventory inventory;
    public Shop(@NotNull Location location, @NotNull ShopInventory inventory){
        this.location = location;
        this.inventory = inventory;
    }

    public void open(Player p){
    }


    //Entity
    public void createEntity(){
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setAI(false);
        villager.setAdult();
        villager.setAgeLock(true);
        villager.setProfession(Villager.Profession.FARMER);
        villager.setCollidable(false);
        villager.setInvulnerable(true);
        villager.setSilent(true);
        this.villager = new EntityStore<>(villager);
    }

    public UUID getEntityID(){
        Villager v = villager.getEntity();
        return v != null ? v.getUniqueId() : null;
    }

    public void removeEntity(){
        Entity ent = this.villager.getEntity();
        if(ent != null){
            ent.remove();
        }
    }

    public Villager getEntity(){
        return villager.getEntity();
    }
}
