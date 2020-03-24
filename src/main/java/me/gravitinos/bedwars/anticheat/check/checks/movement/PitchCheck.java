package me.gravitinos.bedwars.anticheat.check.checks.movement;

import me.gravitinos.bedwars.anticheat.check.AlertType;
import me.gravitinos.bedwars.anticheat.check.Check;
import me.gravitinos.bedwars.anticheat.check.CheckType;
import me.gravitinos.bedwars.anticheat.check.PunishType;
import me.gravitinos.bedwars.anticheat.data.Profile;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import me.gravitinos.bedwars.gamecore.util.PacketEvent;
import net.minecraft.server.v1_12_R1.PacketPlayInFlying;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;

public class PitchCheck extends Check {
    public PitchCheck(Profile profile) {
        super(profile, "Pitch Check", CheckType.PACKET, AlertType.ALL_STAFF, PunishType.EXPERIMENTAL);
    }


    @EventSubscription
    private void onPacket(PacketEvent<PacketPlayInFlying> event){
        if(!event.getPlayer().equals(getProfile().getUniqueId())){
            return;
        }
        Field pitchField;
        try {
            pitchField = event.getPacket().getClass().getDeclaredField("pitch");
            pitchField.setAccessible(true);
            float pitch = (float) pitchField.get(event.getPacket());
            pitchField.setAccessible(false);
            if(Math.abs(pitch) > 90){
                Bukkit.broadcastMessage("Pitch violation detected for " + getProfile().getName());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
        }
    }
}
