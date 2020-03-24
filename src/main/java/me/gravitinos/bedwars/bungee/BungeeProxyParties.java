package me.gravitinos.bedwars.bungee;

import com.google.common.collect.Lists;
import me.gravitinos.bedwars.gamecore.party.BaseParty;
import me.gravitinos.bedwars.gamecore.party.BungeePartyFactory;
import me.gravitinos.bedwars.gamecore.party.messaging.PartyMessageIdentifier;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BungeeProxyParties extends Plugin implements Listener {

    private Map<UUID, BaseParty> parties = new HashMap<>();

    @Override
    public void onEnable() {
        super.onEnable();
        getProxy().getPluginManager().registerListener(this, this);
        getProxy().registerChannel("BungeeCord");
    }

    public BaseParty createParty(UUID leader, UUID partyId, UUID... members) {
        BaseParty party = new BaseParty(leader);
        party.setPartyId(partyId);
        party.setLeader(leader);
        for (UUID member : members) {
            party.addMember(member);
        }
        return party;
    }

    public void removeInvalidParties(){
        for(UUID partyId : Lists.newArrayList(parties.keySet())){
            if(parties.get(partyId).getMembers().size() == 0){
                parties.remove(partyId);
            }
        }
    }

    public BaseParty getPartyOf(UUID member) {
        for (BaseParty party : this.parties.values()) {
            if (party.getMembers().contains(member)) {
                return party;
            }
        }
        return null;
    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        BaseParty party = getPartyOf(event.getPlayer().getUniqueId());
        if (party != null) {
            if (party.getLeader().equals(event.getPlayer().getUniqueId())) {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream stream1 = new DataOutputStream(b);
                try {

                    stream1.writeUTF(PartyMessageIdentifier.SUB_REM_MEMBER);
                    stream1.writeUTF(party.getPartyId().toString());
                    stream1.writeUTF(event.getPlayer().getUniqueId().toString());
                    stream1.writeUTF(event.getPlayer().getName());
                    event.getPlayer().getServer().getInfo().sendData("BungeeCord", b.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getTag().equals("BungeeCord")) {
            DataInputStream stream = new DataInputStream(new ByteArrayInputStream(event.getData()));

            try {
                String cmd = stream.readUTF();

                if (cmd.equals(PartyMessageIdentifier.SUB_PARTY_UPDATE)) {
                    event.setCancelled(true);
                    UUID partyId = UUID.fromString(stream.readUTF());
                    UUID leader = UUID.fromString(stream.readUTF());
                    ArrayList<UUID> mems = new ArrayList<>();
                    String members = stream.readUTF();
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
                    String invited = stream.readUTF();
                    String[] sections = invited.split(", ");
                    for (String section : sections) {
                        String[] elements = section.split(":");
                        if (elements.length < 2) {
                            continue;
                        }
                        try {
                            UUID id = UUID.fromString(elements[0]);
                            long ex = Long.parseLong(elements[1]);
                            invitedPlayers.put(id, ex);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    BaseParty p = createParty(leader, partyId, mems.toArray(new UUID[0]));
                    for (UUID invitedPlayer : invitedPlayers.keySet()) {
                        p.getInvitedPlayers().put(invitedPlayer, invitedPlayers.get(invitedPlayer));
                    }

                    this.parties.put(p.getPartyId(), p);


                    this.removeInvalidParties();

                } else if (cmd.equals(PartyMessageIdentifier.SUB_INVITE)) {
                    event.setCancelled(true);
                    String partyID = stream.readUTF();
                    String userID = stream.readUTF();
                    String inviter = stream.readUTF();

                    if (!this.parties.containsKey(UUID.fromString(partyID))) {
                        return;
                    }
                    BaseParty party = this.parties.get(UUID.fromString(partyID));
                    party.invitePlayer(UUID.fromString(userID));

                    ProxiedPlayer p1 = getProxy().getPlayer(UUID.fromString(userID));
                    ProxiedPlayer sender = getProxy().getPlayer(event.getReceiver().toString());
                    if (sender.getServer().getInfo().getName().equals(p1.getServer().getInfo().getName())) {
                        return;
                    }
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream stream1 = new DataOutputStream(b);
                    stream1.writeUTF(PartyMessageIdentifier.SUB_INVITE);
                    stream1.writeUTF(partyID);
                    stream1.writeUTF(userID);
                    stream1.writeUTF(inviter);
                    stream1.writeUTF(sender.getServer().getInfo().getName());
                    p1.getServer().getInfo().sendData("BungeeCord", b.toByteArray());
                } else if (cmd.equals(PartyMessageIdentifier.SUB_ADD_MEMBER)) {
                    event.setCancelled(true);
                    UUID partyId = UUID.fromString(stream.readUTF());
                    if (!this.parties.containsKey(partyId)) {
                        return;
                    }

                    BaseParty party = this.parties.get(partyId);

                    UUID toAdd = UUID.fromString(stream.readUTF());
                    ProxiedPlayer p = getProxy().getPlayer(toAdd);
                    ProxiedPlayer leader = getProxy().getPlayer(party.getLeader());
                    ProxiedPlayer sender = getProxy().getPlayer(event.getReceiver().toString());


                    BaseParty party1 = getPartyOf(toAdd);
                    if (party1 != null) {
                        party1.removeMember(toAdd);
                    }
                    party.addMember(toAdd);

                    if (!leader.getServer().getInfo().getName().equals(p.getServer().getInfo().getName())) {
                        p.connect(leader.getServer().getInfo());
                    }

                    //Send to leader's server if it didn't come from the leader's server
                    if (!sender.getServer().getInfo().getName().equals(leader.getServer().getInfo().getName())) {
                        ByteArrayOutputStream b = new ByteArrayOutputStream();
                        DataOutputStream stream1 = new DataOutputStream(b);
                        stream1.writeUTF(PartyMessageIdentifier.SUB_ADD_MEMBER);
                        stream1.writeUTF(partyId.toString());
                        stream1.writeUTF(toAdd.toString());
                        stream1.writeUTF(p.getName());
                        stream1.writeUTF(p.getServer().getInfo().getName());

                        leader.getServer().getInfo().sendData("BungeeCord", b.toByteArray());
                    }
                    this.removeInvalidParties();
                } else if (cmd.equals(PartyMessageIdentifier.SUB_REM_MEMBER)) {
                    event.setCancelled(true);


                    UUID partyId = UUID.fromString(stream.readUTF());
                    if (!this.parties.containsKey(partyId)) {
                        return;
                    }

                    BaseParty party = this.parties.get(partyId);

                    UUID toRem = UUID.fromString(stream.readUTF());
                    ProxiedPlayer p = getProxy().getPlayer(toRem);
                    ProxiedPlayer leader = getProxy().getPlayer(party.getLeader());
                    ProxiedPlayer sender = getProxy().getPlayer(event.getReceiver().toString());

                    party.removeMember(toRem);

                    //Send to leader's server if it didn't come from the leader's server
                    if (!sender.getServer().getInfo().getName().equals(leader.getServer().getInfo().getName())) {
                        ByteArrayOutputStream b = new ByteArrayOutputStream();
                        DataOutputStream stream1 = new DataOutputStream(b);
                        stream1.writeUTF(PartyMessageIdentifier.SUB_REM_MEMBER);
                        stream1.writeUTF(partyId.toString());
                        stream1.writeUTF(toRem.toString());
                        stream1.writeUTF(p.getName());
                        leader.getServer().getInfo().sendData("BungeeCord", b.toByteArray());
                    }
                    this.removeInvalidParties();
                } else if (cmd.equals(PartyMessageIdentifier.SUB_SETLEADER)) {
                    event.setCancelled(true);

                    UUID partyId = UUID.fromString(stream.readUTF());
                    if (!this.parties.containsKey(partyId)) {
                        return;
                    }

                    BaseParty party = this.parties.get(partyId);

                    UUID toSet = UUID.fromString(stream.readUTF());

                    ProxiedPlayer p = getProxy().getPlayer(toSet);

                    party.addMember(toSet);

                    BaseParty party1 = getPartyOf(toSet);
                    if (party1 != null) {
                        party1.removeMember(toSet);
                    }

                    party.setLeader(toSet);

                    //Send all players to leader's server
                    this.sendMembersToServer(party, p.getServer().getInfo());


                    //------------This commented out section would send a message to the new leader's server to notify it of the leader setting operation-------------

//                    if(!p.getServer().getInfo().getName().equals(getProxy().getPlayer(event.getReceiver().toString()).getServer().getInfo().getName())){
//                        ByteArrayOutputStream b = new ByteArrayOutputStream();
//                        DataOutputStream stream1 = new DataOutputStream(b);
//                        stream1.writeUTF(PartyMessageIdentifier.CHANNEL_RECEIVE);
//                        stream1.writeUTF(PartyMessageIdentifier.SUB_SETLEADER);
//                        stream1.writeUTF(partyId.toString());
//                        stream1.writeUTF(toSet.toString());
//                        p.getServer().getInfo().sendData("BungeeCord", b.toByteArray());
//                    }
                    this.removeInvalidParties();
                } else if (cmd.equals(PartyMessageIdentifier.SUB_GET) || cmd.equals(PartyMessageIdentifier.SUB_GET_FROM_MEMBER)) {
                    event.setCancelled(true);

                    UUID partyId = UUID.fromString(stream.readUTF());

                    ProxiedPlayer sender = getProxy().getPlayer(event.getReceiver().toString());


                    if (!this.parties.containsKey(partyId) && getPartyOf(partyId) == null) {
                        ByteArrayOutputStream b = new ByteArrayOutputStream();
                        DataOutputStream stream1 = new DataOutputStream(b);
                        stream1.writeUTF(cmd);
                        stream1.writeUTF(partyId.toString());
                        if(cmd.equals(PartyMessageIdentifier.SUB_GET_FROM_MEMBER)){
                            stream1.writeUTF(partyId.toString());
                        }
                        stream1.writeUTF("PARTY_NOT_FOUND");
                        sender.getServer().getInfo().sendData("BungeeCord", b.toByteArray());
                        return;
                    }

                    BaseParty party = cmd.equals(PartyMessageIdentifier.SUB_GET) ? this.parties.get(partyId) : getPartyOf(partyId);
                    if(party == null){
                        party = !cmd.equals(PartyMessageIdentifier.SUB_GET) ? this.parties.get(partyId) : getPartyOf(partyId);
                    }

                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream stream1 = new DataOutputStream(b);
                    stream1.writeUTF(cmd);
                    stream1.writeUTF(partyId.toString());
                    if (cmd.equals(PartyMessageIdentifier.SUB_GET_FROM_MEMBER)) {
                        stream1.writeUTF(party.getPartyId().toString());
                    }

                    StringBuilder members = new StringBuilder();
                    for (UUID member : party.getMembers()) {
                        members.append(member.toString()).append(", ");
                    }

                    StringBuilder invited = new StringBuilder();
                    party.getInvitedPlayers().forEach((ip, time) -> invited.append(ip.toString()).append(":").append(time).append(", "));

                    stream1.writeUTF(party.getLeader().toString());
                    stream1.writeUTF(members.toString());
                    stream1.writeUTF(invited.toString());


                    sender.getServer().getInfo().sendData("BungeeCord", b.toByteArray());

                } else if (cmd.equals(PartyMessageIdentifier.SUB_UNINVITE)) {
                    event.setCancelled(true);

                    UUID partyId = UUID.fromString(stream.readUTF());
                    if (!this.parties.containsKey(partyId)) {
                        return;
                    }

                    BaseParty party = this.parties.get(partyId);

                    UUID toRem = UUID.fromString(stream.readUTF());
                    ProxiedPlayer p = getProxy().getPlayer(toRem);
                    ProxiedPlayer leader = getProxy().getPlayer(party.getLeader());
                    ProxiedPlayer sender = getProxy().getPlayer(event.getReceiver().toString());

                    party.removeInvite(toRem);

                    //Send to leader's server if it didn't come from the leader's server
                    if (!sender.getServer().getInfo().getName().equals(leader.getServer().getInfo().getName())) {
                        ByteArrayOutputStream b = new ByteArrayOutputStream();
                        DataOutputStream stream1 = new DataOutputStream(b);
                        stream1.writeUTF(PartyMessageIdentifier.SUB_UNINVITE);
                        stream1.writeUTF(partyId.toString());
                        stream1.writeUTF(toRem.toString());
                        stream1.writeUTF(p.getName());
                        leader.getServer().getInfo().sendData("BungeeCord", b.toByteArray());
                    }
                    this.removeInvalidParties();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @EventHandler
    public void onSwitchServer(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();
        for (BaseParty parties : this.parties.values()) {
            if (parties.getLeader().equals(player.getUniqueId())) {
                this.sendMembersToServer(parties, event.getServer().getInfo());
                return;
            }
        }
    }

    public void sendMembersToServer(@NotNull BaseParty party, @NotNull ServerInfo server) {
        party.getMembers().forEach(m -> {
            if(m.equals(party.getLeader())){
                return;
            }
            ProxiedPlayer member = getProxy().getPlayer(m);
            if(member.getServer() != null){
                if(member.getServer().getInfo() != null){
                    if(member.getServer().getInfo().getName().equals(server.getName())){
                        return;
                    }
                }
            }
            member.connect(server, ServerConnectEvent.Reason.PLUGIN);
            member.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(TextComponent.fromLegacyText(BungeePartyFactory.PREFIX + "You have been connected to your party leader's server")));
            member.sendMessage(ChatMessageType.CHAT, new TextComponent(TextComponent.fromLegacyText(BungeePartyFactory.PREFIX + "You have been connected to your party leader's server")));
        });
    }

}
