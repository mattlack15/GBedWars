package me.gravitinos.bedwars.gamecore.party.messaging.agents;

import com.google.common.collect.Lists;
import me.gravitinos.bedwars.gamecore.channel.MessageAgent;
import me.gravitinos.bedwars.gamecore.channel.MessageCallback;
import me.gravitinos.bedwars.gamecore.party.BaseParty;
import me.gravitinos.bedwars.gamecore.party.messaging.PartyMessageIdentifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PartyMessageUpdate implements MessageAgent {

    private BaseParty party;

    public PartyMessageUpdate(BaseParty party){
        this.party = party;
    }

    @Override
    public String getSubChannel() {
        return PartyMessageIdentifier.SUB_PARTY_UPDATE;
    }

    @Override
    public void appendPayload(DataOutputStream outputStream) throws IOException {
        StringBuilder members = new StringBuilder();
        for (UUID member : party.getMembers()) {
            members.append(member.toString()).append(", ");
        }

        StringBuilder invited = new StringBuilder();
        party.getInvitedPlayers().forEach((ip, time) -> invited.append(ip.toString()).append(":").append(time).append(", "));

        outputStream.writeUTF(party.getPartyId().toString());
        outputStream.writeUTF(party.getLeader().toString());
        outputStream.writeUTF(members.toString());
        outputStream.writeUTF(invited.toString());
    }

    @Override
    public Player getPlayer() {
        return Lists.newArrayList(Bukkit.getOnlinePlayers()).get(0);
    }
}
