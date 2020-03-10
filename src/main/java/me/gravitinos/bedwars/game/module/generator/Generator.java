package me.gravitinos.bedwars.game.module.generator;

import me.gravitinos.bedwars.gamecore.util.ArmorStandFactory;
import me.gravitinos.bedwars.gamecore.util.ArmorStandTextHolder;
import me.gravitinos.bedwars.gamecore.util.EntityStore;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Generator implements ArmorStandTextHolder {
    public double countdown;
    private String name;
    private ItemStack drop;
    private double interval;
    private Location location;
    private EntityStore<ArmorStand> stand = new EntityStore<>(null);
    private EntityStore<ArmorStand> item = new EntityStore<>(null);
    private ItemStack displayItem = null;
    private boolean multipleItemGiving = false;

    public Generator(@NotNull Location location, @NotNull String name, @NotNull ItemStack drop, double interval) {
        this.name = name;
        this.drop = drop;
        this.interval = interval;
        countdown = interval;
        this.location = location;
    }

    public Generator setDisplayItem(@NotNull ItemStack stack){
        this.displayItem = stack;
        ArmorStand stand = this.getItem();
        if(stand != null){
            stand.setHelmet(stack);
        }
        return this;
    }

    public boolean isMultipleItemGiving() {
        return multipleItemGiving;
    }

    public Generator setMultipleItemGiving(boolean multipleItemGiving) {
        this.multipleItemGiving = multipleItemGiving;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public double getInterval() {
        return this.interval;
    }

    public Generator setInterval(int interval) {
        this.interval = interval;
        return this;
    }

    public Location getLocation() {
        return this.location;
    }

    public ItemStack getDrop() {
        return this.drop;
    }

    public Generator setDrop(ItemStack drop) {
        this.drop = drop;
        return this;
    }

    public void dropItem(int amount) {
        ItemStack stack = drop.clone();
        stack.setAmount(amount);
        boolean playerFound = false;
        if(multipleItemGiving) {
            for (Entity ents : location.getWorld().getNearbyEntities(location.clone().add(0.5, 0, 0.5), 1.6, 2, 1.6)) {
                if (ents instanceof Player) {
                    playerFound = true;
                    Player p = (Player) ents;
                    p.getInventory().addItem(stack);
                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 2f, 1f);
                }
            }
        }
        if (!playerFound) {
            this.location.getWorld().dropItem(location.clone().add(0.5, 0.05, 0.5), stack).setVelocity(new Vector(0, 0, 0));
        }
    }

    //Item

    public void createItem() {
        this.removeItem();
        Location loc = location.clone().add(0.5, 2.2, 0.5);
        ArmorStand item1 = ArmorStandFactory.createHidden(loc);
        item1.setGravity(false);
        item1.setHelmet(this.displayItem);
        item1.setVelocity(new Vector(0, 0, 0));
        item1.setInvulnerable(true);
        item1.setSmall(false);
        this.item = new EntityStore<>(item1);
    }

    public ArmorStand getItem() {
        return this.item.getEntity();
    }

    public void removeItem() {
        ArmorStand stand = this.item.getEntity();
        if (stand != null) {
            stand.remove();
        }
    }

    //Armor Stand

    @Override
    public void removeStand() {
        ArmorStand st = stand.getEntity();
        if (st == null) {
            return;
        }
        st.remove();
    }

    @Override
    public void createStand(String text) {
        this.removeStand();
        Location loc = location.clone().add(0.5, 2.2, 0.5);
        this.stand = new EntityStore<>(ArmorStandFactory.createText(loc, text));
    }

    @Override
    public ArmorStand getStand() {
        return this.stand.getEntity();
    }

    @Override
    public void setText(String text) {
        ArmorStand stand = this.stand.getEntity();
        if (stand != null) {
            stand.setCustomName(text);
            stand.setCustomNameVisible(true);
        }
    }
}
