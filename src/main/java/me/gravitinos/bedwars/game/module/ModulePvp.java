package me.gravitinos.bedwars.game.module;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.module.GameHandler;
import me.gravitinos.bedwars.gamecore.module.GameModule;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ModulePvp extends GameModule {

    private PacketAdapter packetAdapter = new PacketAdapter(CoreHandler.main, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
        @Override
        public void onPacketSending(PacketEvent event) {
            if(!getGameHandler().isRunning()){
                return;
            }
            PacketContainer container = event.getPacket();
            Sound sound = container.getSoundEffects().read(0);
            if(sound.equals(Sound.ENTITY_PLAYER_HURT)) {
                event.setCancelled(true);
            }
        }
    };

    public ModulePvp(GameHandler gameHandler) {
        super(gameHandler, "PVP");
    }

    public void enable(){
        super.enable();
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        pm.addPacketListener(packetAdapter);
    }

    public void disable(){
        super.disable();
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        pm.removePacketListener(packetAdapter);
    }
}
