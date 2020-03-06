package me.gravitinos.bedwars.gamecore.util.Saving;

import com.google.common.collect.Lists;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SavedPlayerState {
    private SavedInventory inventory;
    private Location location;
    private double health;
    private double maxHealth;
    private boolean canFly;
    private boolean isFlying;
    private float flySpeed;
    private int foodLevel;
    private float saturation;
    private int fireTicks;
    private GameMode gameMode;
    private Vector velocity;
    private ArrayList<PotionEffect> effects;
    private float fall;

    public SavedPlayerState(Player p){
        this.inventory = new SavedInventory(p.getInventory().getContents(), p.getInventory().getArmorContents());
        this.location = p.getLocation().clone();
        this.health = p.getHealth();
        this.maxHealth = p.getMaxHealth();
        this.canFly = p.getAllowFlight();
        this.isFlying = p.isFlying();
        this.flySpeed = p.getFlySpeed();
        this.foodLevel = p.getFoodLevel();
        this.saturation = p.getSaturation();
        this.gameMode = p.getGameMode();
        this.velocity = p.getVelocity().clone();
        this.effects = Lists.newArrayList(p.getActivePotionEffects());
        this.fireTicks = p.getFireTicks();
        this.fall = p.getFallDistance();
    }

    /**
     * Restores this state to the given player
     * @param p The player to restore to
     * @return The player's current player state
     */
    public SavedPlayerState restore(Player p){
        SavedPlayerState state = new SavedPlayerState(p);
        this.inventory.restore(p);
        p.teleport(location, PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
        p.setMaxHealth(this.maxHealth);
        p.setHealth(this.health);
        p.setGameMode(this.gameMode);
        p.setAllowFlight(this.canFly);
        if(this.canFly){
            p.setFlying(this.isFlying);
        } else {
            p.setFlying(false);
        }
        p.setVelocity(this.velocity);
        p.setFoodLevel(this.foodLevel);
        p.setSaturation(this.saturation);
        p.getActivePotionEffects().stream().forEach(e -> p.removePotionEffect(e.getType()));
        effects.forEach(p::addPotionEffect);
        p.setFireTicks(fireTicks);
        p.setFallDistance(fall);
        return state;
    }

    public void setFlySpeed(float flySpeed) {
        this.flySpeed = flySpeed;
    }

    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public void setVelocity(@NotNull Vector velocity) {
        this.velocity = velocity.clone();
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setCanFly(boolean canFly) {
        this.canFly = canFly;
    }

    public boolean isCanFly() {
        return canFly;
    }

    public boolean isFlying() {
        return isFlying;
    }

    public SavedInventory getInventory() {
        return inventory;
    }

    public double getHealth() {
        return health;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public Location getLocation() {
        return location;
    }

    public void setFlying(boolean flying) {
        isFlying = flying;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public float getFlySpeed() {
        return flySpeed;
    }

    public float getSaturation() {
        return saturation;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public void setInventory(SavedInventory inventory) {
        this.inventory = inventory;
    }

    public void setLocation(Location location) { this.location = location; }

}
