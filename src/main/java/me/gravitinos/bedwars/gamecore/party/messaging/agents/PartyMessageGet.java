package me.gravitinos.bedwars.gamecore.party.messaging.agents;

import com.google.common.collect.Lists;
import me.gravitinos.bedwars.gamecore.channel.MessageAgent;
import me.gravitinos.bedwars.gamecore.channel.MessageCallback;
import me.gravitinos.bedwars.gamecore.party.BungeeParty;
import me.gravitinos.bedwars.gamecore.party.BungeePartyFactory;
import me.gravitinos.bedwars.gamecore.party.messaging.PartyMessageIdentifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class PartyMessageGet implements MessageAgent, MessageCallback {
    private UUID partyId;
    private Consumer<BungeeParty> callback;

    public PartyMessageGet(UUID partyId, Consumer<BungeeParty> callback){
        this.partyId = partyId;
        this.callback = callback;
    }

    @Override
    public String getSubChannel() {
        return PartyMessageIdentifier.SUB_GET;
    }

    @Override
    public boolean test(Player receiver, DataInputStream input) throws IOException {
        return this.partyId.toString().equals(input.readUTF());
    }

    @Override
    public boolean accept(Player receiver, DataInputStream input) throws IOException {
        input.readUTF();
        String ls = input.readUTF();
        if(ls.equals("PARTY_NOT_FOUND")){
            this.callback.accept(null);
            return true;
        }
        UUID leader = UUID.fromString(ls);
        ArrayList<UUID> mems = new ArrayList<>();
        String members = input.readUTF();
        members = members.replace("\"", "");
        for (String mem : members.split(", ")) {
            try {
                UUID id = UUID.fromString(mem);
                mems.add(id);
            } catch (Throwable ignored) {
            }
        }

        //Decode invited players
        Map<UUID, Long> invitedPlayers = new HashMap<>();
        String invited = input.readUTF();
        String[] sections = invited.split(", ");
        for(String section : sections){
            String[] elements = section.split(":");
            if(elements.length < 2){
                continue;
            }
            try{
                UUID id = UUID.fromString(elements[0]);
                long ex = Long.parseLong(elements[1]);
                invitedPlayers.put(id, ex);
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        BungeeParty p = BungeePartyFactory.createNewUnregisteredParty(leader, partyId, mems.toArray(new UUID[0]));
        for (UUID invitedPlayer : invitedPlayers.keySet()) {
            p.getInvitedPlayers().put(invitedPlayer, invitedPlayers.get(invitedPlayer));
        }

        this.callback.accept(p);
        return true;
    }

    @Override
    public void appendPayload(DataOutputStream outputStream) throws IOException {
        outputStream.writeUTF(partyId.toString());
    }

    @Override
    public Player getPlayer() {
        return Lists.newArrayList(Bukkit.getOnlinePlayers()).get(0);
    }
}
