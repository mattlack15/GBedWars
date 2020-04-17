package me.gravitinos.minigame.gamecore.party.messaging.agents;

import com.google.common.collect.Lists;
import me.gravitinos.minigame.gamecore.channel.MessageAgent;
import me.gravitinos.minigame.gamecore.party.messaging.PartyMessageIdentifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PartyMessageAddMember implements MessageAgent {

    private UUID partyId;
    private UUID member;

    public PartyMessageAddMember(UUID partyId, UUID member){
        this.partyId = partyId;
        this.member = member;
    }

    @Override
    public String getSubChannel() {
        return PartyMessageIdentifier.SUB_ADD_MEMBER;
    }

    @Override
    public void appendPayload(DataOutputStream outputStream) throws IOException {
        outputStream.writeUTF(partyId.toString());
        outputStream.writeUTF(member.toString());
    }

    @Override
    public Player getPlayer() {
        return Lists.newArrayList(Bukkit.getOnlinePlayers()).get(0);
    }
}
