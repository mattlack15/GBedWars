package me.gravitinos.minigame.bedwars.anticheat.check.checks.movement;

import me.gravitinos.minigame.bedwars.anticheat.check.AlertType;
import me.gravitinos.minigame.bedwars.anticheat.check.Check;
import me.gravitinos.minigame.bedwars.anticheat.check.CheckType;
import me.gravitinos.minigame.bedwars.anticheat.check.PunishType;
import me.gravitinos.minigame.bedwars.anticheat.data.Profile;
import me.gravitinos.minigame.bedwars.anticheat.data.Violation;
import me.gravitinos.minigame.gamecore.util.EventSubscription;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class FlightA extends Check {

    private boolean lastOnGround, lastLastOnGround;

    private boolean lastOnGroundU, lastLastOnGroundU;

    private double lastDistY;

    private double lastCombined, lastLastCombined;

    private boolean hadEntitiesNearby = false;

    private long noCheckUntil = System.currentTimeMillis();

    public FlightA(Profile profile) {
        super(profile, "Flight A", CheckType.MOVEMENT, AlertType.ALL_STAFF, PunishType.EXPERIMENTAL);
    }

    @EventSubscription
    private void onMove(PlayerMoveEvent event){
        if(!event.getPlayer().getUniqueId().equals(getProfile().getUniqueId()) || event.getPlayer().getAllowFlight() || System.currentTimeMillis() < noCheckUntil){
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();

        double disY = to.getY() - from.getY();

        double lastDistY = this.lastDistY;

        this.lastDistY = disY;

        double predictedDistY = (lastDistY - 0.08D) * 0.9800000190734863D;
        if(Math.abs(predictedDistY) < 0.005d){
            predictedDistY = disY;
        }

        double disX = to.getX() - from.getX();
        double disZ = to.getZ() - from.getZ();

        double combined = Math.abs(disX) + Math.abs(disZ);

        double lastCombined = this.lastCombined;
        this.lastCombined = combined;

        double lastLastCombined =  this.lastLastCombined;
        this.lastLastCombined = lastCombined;

        if(Math.abs(disY) < 0.3 && (combined < 0.08 || lastCombined < 0.08 || lastLastCombined < 0.08)){
            predictedDistY = disY;
        }

        boolean nearGround = isNearGround(to, false);

        boolean lastOnGround = this.lastOnGround;
        this.lastOnGround = nearGround;

        boolean lastLastOnGround = this.lastLastOnGround;
        this.lastLastOnGround = lastOnGround;

        boolean nearGroundU = isNearGround(to.clone().add(0, 2, 0), true);

        boolean lastOnGroundU = this.lastOnGroundU;
        this.lastOnGroundU = nearGroundU;

        boolean lastLastOnGroundU = this.lastLastOnGroundU;
        this.lastLastOnGroundU = lastOnGroundU;

        hadEntitiesNearby = to.getWorld().getNearbyEntities(to, 5, 5, 5).size() != 0;

        if(!nearGround && !lastOnGround && !lastLastOnGround && !lastOnGroundU && !nearGroundU && !lastLastOnGroundU){
            if(!isRoughlyEqual(disY, predictedDistY)){
                addViolation(new Violation(1));
            }
        }

    }

    private double roundTo(double d, int decimalPlaces){
        return Math.round(d * decimalPlaces * 10) / (d * 10);
    }

    @EventSubscription
    private void onHit(EntityDamageEvent event) {
        if (!hadEntitiesNearby && !event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            if (event.getEntity().getUniqueId().equals(getProfile().getUniqueId())) {
                this.lastOnGround = true;
                this.clearViolationsPast(-1);
                this.noCheckUntil = System.currentTimeMillis() + 1000;
            }
        }
    }

    @EventSubscription
    private void onTeleport(PlayerTeleportEvent event){
        if(event.getPlayer().getUniqueId().equals(getProfile().getUniqueId())){
            this.lastLastOnGround = true;
            this.lastOnGround = true;
            this.clearViolationsPast(-1);
            this.noCheckUntil = System.currentTimeMillis() + 2000;
        }
    }

    private boolean isRoughlyEqual(double a, double b){
        return Math.abs(a - b) < 0.01d;
    }

    private boolean isNearGround(Location location, boolean checkUp) {
        double expand = 0.3;
        double a = checkUp ? 0.0001 : -0.0001;
        for (int i = 0; i < 4; i++) {
                for (double x = -expand; x <= expand; x += expand) {
                    for (double z = -expand; z <= expand; z += expand) {
                        if (!location.clone().add(x, a, z).getBlock().getType().equals(Material.AIR)) {
                            return true;
                        }
                    }
                }
                if (checkUp) {
                    a += 0.5;
                } else {
                    a -= 0.5;
                }
        }
        return false;
    }

}
