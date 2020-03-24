package me.gravitinos.bedwars.gamecore.party.messaging.agents.listeners;

import com.google.common.collect.Lists;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.channel.MessageAgent;
import me.gravitinos.bedwars.gamecore.channel.MessageCallback;
import me.gravitinos.bedwars.gamecore.party.BungeeParty;
import me.gravitinos.bedwars.gamecore.party.BungeePartyFactory;
import me.gravitinos.bedwars.gamecore.party.messaging.PartyMessageIdentifier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PartyMessageAddMemberListener implements MessageCallback {

    @Override
    public String getSubChannel() {
        return PartyMessageIdentifier.SUB_ADD_MEMBER;
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
                UUID member = UUID.fromString(input.readUTF());
                String name = input.readUTF();
                String sourceServer = input.readUTF();

                BungeeParty party = BungeePartyFactory.getRegisteredParty(partyId);
                if(party != null){
                    party.addMemberLocal(member);
                    party.getMembers().forEach(m -> {
                        Player p = Bukkit.getPlayer(m);
                        if(p == null) return;
                        p.sendMessage(BungeePartyFactory.PREFIX + ChatColor.GREEN + name + ChatColor.GRAY + " is joining the party from " + ChatColor.YELLOW + sourceServer);
                    });
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        });

        return false;
    }
}
