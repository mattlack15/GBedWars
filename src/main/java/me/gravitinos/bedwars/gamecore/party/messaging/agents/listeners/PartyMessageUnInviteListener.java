package me.gravitinos.bedwars.gamecore.party.messaging.agents.listeners;

import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.channel.MessageCallback;
import me.gravitinos.bedwars.gamecore.party.BungeeParty;
import me.gravitinos.bedwars.gamecore.party.BungeePartyFactory;
import me.gravitinos.bedwars.gamecore.party.messaging.PartyMessageIdentifier;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class PartyMessageUnInviteListener implements MessageCallback {

    @Override
    public String getSubChannel() {
        return PartyMessageIdentifier.SUB_UNINVITE;
    }

    @Override
    public boolean test(Player receiver, DataInputStream input) throws IOException {
        return true;
    }

    @Override
    public boolean accept(Player receiver, DataInputStream input) throws IOException {

        CoreHandler.instance.getAsyncExecutor().execute(() -> {
            try {
                UUID partyId = UUID.fromString(input.readUTF());
                UUID player = UUID.fromString(input.readUTF());

                BungeeParty p = BungeePartyFactory.getRegisteredParty(partyId);
                if(p != null) p.removeInviteLocal(player);
            }catch(Exception e){
                e.printStackTrace();
            }
        });

        return false;
    }
}
