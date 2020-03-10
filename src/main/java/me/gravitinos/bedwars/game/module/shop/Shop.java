package me.gravitinos.bedwars.game.module.shop;

import me.gravitinos.bedwars.gamecore.util.EntityStore;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Shop {
    private Location location;
    private EntityStore<Villager> villager = new EntityStore<>(null);
    private ShopInventory inventory;
    private String displayName;
    public Shop(@NotNull String displayName, @NotNull Location location, @NotNull ShopInventory inventory){
        this.location = location;
        this.inventory = inventory;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void open(Player p){
        new ShopMenu(this).open(p);
    }

    public ShopInventory getInventory() {
        return this.inventory;
    }

    public void setInventory(ShopInventory inventory) {
        this.inventory = inventory;
    }

    //Entity
    public void createEntity(@Nullable Location facing){
        Location spawnLocation = location.clone().add(0.5, 0, 0.5);
        if(facing != null) {
            spawnLocation.setDirection(facing.toVector().subtract(spawnLocation.toVector()).normalize());
        }

        Villager villager = (Villager) location.getWorld().spawnEntity(spawnLocation, EntityType.VILLAGER);
        villager.setAI(false);
        villager.setAdult();
        villager.setAgeLock(true);
        villager.setProfession(Villager.Profession.FARMER);
        villager.setCollidable(false);
        villager.setInvulnerable(true);
        villager.setSilent(true);
        villager.setCustomName(ChatColor.translateAlternateColorCodes('&', this.displayName));
        villager.setCustomNameVisible(true);
        villager.setRemoveWhenFarAway(false);
        this.villager = new EntityStore<>(villager);
    }

    public UUID getEntityUUID(){
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
