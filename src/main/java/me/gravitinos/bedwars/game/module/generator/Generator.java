package me.gravitinos.bedwars.game.module.generator;

import me.gravitinos.bedwars.gamecore.util.ArmorStandFactory;
import me.gravitinos.bedwars.gamecore.util.ArmorStandTextHolder;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Generator implements ArmorStandTextHolder {
    public double countdown;
    private String name;
    private ItemStack drop;
    private double interval;
    private Location location;
    private ArmorStand stand = null;
    private Item item = null;
    public Generator(@NotNull Location location, @NotNull String name, @NotNull ItemStack drop, double interval){
        this.name = name;
        this.drop = drop;
        this.interval = interval;
        countdown = interval;
        this.location = location;
    }

    public String getName(){
        return this.name;
    }

    public double getInterval(){
        return this.interval;
    }

    public void setInterval(int interval){
        this.interval = interval;
    }

    public Location getLocation(){
        return this.location;
    }

    public ItemStack getDrop(){
        return this.drop;
    }

    public void setDrop(ItemStack drop){
        this.drop = drop;
    }

    public void dropItem(int amount){
        ItemStack stack = drop.clone();
        stack.setAmount(amount);
        this.location.getWorld().dropItem(location.clone().add(0.5, 0.05, 0.5), stack).setVelocity(new Vector(0,0,0));
    }

    //Item

    public void createItem(){
        this.removeItem();
        Location loc = location.clone().add(0.5, 3.2, 0.5);
        this.item = loc.getWorld().dropItem(loc, drop);
        this.item.setGravity(false);
        this.item.setVelocity(new Vector(0,0,0));
        this.item.setInvulnerable(true);
    }

    public Item getItem(){
        return this.item;
    }

    public void removeItem(){
        if(this.item != null){
            item.remove();
        }
    }

    //Armor Stand

    @Override
    public void removeStand() {
        if(stand == null){
            return;
        }
        stand.remove();
    }

    @Override
    public void createStand(String text) {
        this.removeStand();
        Location loc = location.clone().add(0.5, 2.2, 0.5);
        this.stand = ArmorStandFactory.createText(loc, text);
    }

    @Override
    public ArmorStand getStand() {
        return this.stand;
    }

    @Override
    public void setText(String text) {
        if(this.stand != null){
            stand.setCustomName(text);
        }
    }
}
