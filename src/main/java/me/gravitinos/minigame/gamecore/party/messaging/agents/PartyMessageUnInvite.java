package me.gravitinos.minigame.gamecore.party.messaging.agents;

import com.google.common.collect.Lists;
import me.gravitinos.minigame.gamecore.channel.MessageAgent;
import me.gravitinos.minigame.gamecore.party.messaging.PartyMessageIdentifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PartyMessageUnInvite implements MessageAgent {

    private UUID partyId;
    private UUID player;

    public PartyMessageUnInvite(UUID partyId, UUID player){
        this.partyId = partyId;
        this.player = player;
    }

    @Override
    public String getSubChannel() {
        return PartyMessageIdentifier.SUB_UNINVITE;
    }


    @Override
    public void appendPayload(DataOutputStream outputStream) throws IOException {
        outputStream.writeUTF(partyId.toString());
        outputStream.writeUTF(player.toString());
    }

    @Override
    public Player getPlayer() {
        return Lists.newArrayList(Bukkit.getOnlinePlayers()).get(0);
    }
}
