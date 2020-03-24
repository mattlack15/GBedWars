package me.gravitinos.bedwars.gamecore.party.messaging.agents;

import com.google.common.collect.Lists;
import me.gravitinos.bedwars.gamecore.channel.MessageAgent;
import me.gravitinos.bedwars.gamecore.channel.MessageCallback;
import me.gravitinos.bedwars.gamecore.party.PartyComm;
import me.gravitinos.bedwars.gamecore.party.messaging.PartyMessageIdentifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PartyMessageRemoveMember implements MessageAgent {

    private UUID partyId;
    private UUID member;

    public PartyMessageRemoveMember(UUID partyId, UUID member){
        this.partyId = partyId;
        this.member = member;
    }

    @Override
    public String getSubChannel() {
        return PartyMessageIdentifier.SUB_REM_MEMBER;
    }

    @Override
    public void appendPayload(DataOutputStream outputStream) throws IOException {
        outputStream.writeUTF(partyId.toString());
        outputStream.writeUTF(member.toString());
    }

    @Override
    public Player getPlayer() {
        if(Bukkit.getOnlinePlayers().size() > 0) {
            return Lists.newArrayList(Bukkit.getOnlinePlayers()).get(0);
        } else{
            return PartyComm.pl;
        }
    }
}
