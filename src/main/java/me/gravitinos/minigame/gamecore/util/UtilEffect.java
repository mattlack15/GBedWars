package me.gravitinos.minigame.gamecore.util;

import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UtilEffect {
    /**
     * Create a beam of particles between two points
     * @param particle Particle type
     * @param from Point 1
     * @param to Point 2
     * @param distanceBetweenParticles Particle distance
     * @param data Additional particle data
     */
    public static void particleBeam(EnumParticle particle, Location from, @NotNull Location to, float distanceBetweenParticles, @NotNull float... data){
        List<PacketPlayOutWorldParticles> packetsToSend = new ArrayList<>();
        Vector direction = to.clone().subtract(from).toVector().normalize();
        float[] offsets = new float[3];
        for(int i = 0; i < data.length; i++){
            if(i >= offsets.length){
                break;
            }
            offsets[i] = data[i];
        }
        for(float i = 0; i < 100; i += distanceBetweenParticles){
            Location loc = from.clone().add(direction.clone().multiply(i));
            if(loc.distanceSquared(to) <= Math.pow(distanceBetweenParticles, 2)){
                break;
            }
            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true, (float)loc.getX(), (float)loc.getY(), (float)loc.getZ(), offsets[0], offsets[1], offsets[2], (float)1, 0, 1);
            packetsToSend.add(packet);
        }
        for(Entity entities : from.getWorld().getNearbyEntities(from, 100,100,100)){
            if(entities instanceof Player){
                for(PacketPlayOutWorldParticles packets : packetsToSend) {
                    ((CraftPlayer) entities).getHandle().playerConnection.sendPacket(packets);
                }
            }
        }
    }

    /**
     * Spawn a specific amount of particles at a location
     * @param particle Particle Type
     * @param loc Location
     * @param amount Amount of particles
     */
    public static void spawnParticles(EnumParticle particle, Location loc, int amount){
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true, (float)loc.getX(), (float)loc.getY(), (float)loc.getZ(), 0.6f, 0.6f, 0.6f, 0, amount);
        for(Entity entities : loc.getWorld().getNearbyEntities(loc, 100,100,100)){
            if(entities instanceof Player){
                ((CraftPlayer) entities).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

}
