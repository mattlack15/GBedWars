package me.gravitinos.bedwars.gamecore.party.messaging.agents;

import com.google.common.collect.Lists;
import me.gravitinos.bedwars.gamecore.channel.MessageAgent;
import me.gravitinos.bedwars.gamecore.channel.MessageCallback;
import me.gravitinos.bedwars.gamecore.party.messaging.PartyMessageIdentifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PartyMessageInvite implements MessageAgent {
    private UUID partyId;
    private UUID player;
    private String inviter;

    public PartyMessageInvite(UUID partyId, UUID player, String inviter){
        this.partyId = partyId;
        this.player = player;
        this.inviter = inviter;
    }

    @Override
    public String getSubChannel() {
        return PartyMessageIdentifier.SUB_INVITE;
    }

    @Override
    public void appendPayload(DataOutputStream outputStream) throws IOException {
        outputStream.writeUTF(partyId.toString());
        outputStream.writeUTF(player.toString());
        outputStream.writeUTF(inviter);
    }

    @Override
    public Player getPlayer() {
        return Lists.newArrayList(Bukkit.getOnlinePlayers()).get(0);
    }
}
