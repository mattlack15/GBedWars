package me.gravitinos.bedwars.gamecore.party;

import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.channel.ProxyComm;
import me.gravitinos.bedwars.gamecore.party.messaging.agents.*;
import me.gravitinos.bedwars.gamecore.party.messaging.agents.listeners.PartyMessageAddMemberListener;
import me.gravitinos.bedwars.gamecore.party.messaging.agents.listeners.PartyMessageInviteListener;
import me.gravitinos.bedwars.gamecore.party.messaging.agents.listeners.PartyMessageRemoveMemberListener;
import me.gravitinos.bedwars.gamecore.party.messaging.agents.listeners.PartyMessageUnInviteListener;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import me.gravitinos.bedwars.gamecore.util.EventSubscriptions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PartyComm {
    public static PartyComm instance;

    public ArrayList<UUID> loaded = new ArrayList<>();

    public static Player pl = null;

    public PartyComm() {
        instance = this;

        EventSubscriptions.instance.subscribe(this);

        ProxyComm.instance.registerListener(new PartyMessageInviteListener());
        ProxyComm.instance.registerListener(new PartyMessageAddMemberListener());
        ProxyComm.instance.registerListener(new PartyMessageUnInviteListener());
        ProxyComm.instance.registerListener(new PartyMessageRemoveMemberListener());

        BaseParty.addMemberMappingListener((m, pid) -> {
            UUID current = BaseParty.findParty(m);
            Player mappedPlayer = Bukkit.getPlayer(m);
            if (mappedPlayer == null) return;
            if (current != null) {
                BaseParty party = BungeePartyFactory.getRegisteredParty(current, false);
                if (party != null) {
                    party.getMembers().forEach(mem -> {
                        Player p = Bukkit.getPlayer(mem);
                        if (p != null && !p.getUniqueId().equals(m)) {
                            p.sendMessage(BungeePartyFactory.PREFIX + ChatColor.WHITE + mappedPlayer.getName() + ChatColor.GRAY + " has left the party!");
                        }
                    });
                }
            }
        });

        CoreHandler.instance.getAsyncExecutor().execute(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                CoreHandler.instance.getAsyncExecutor().execute(() -> {
                    BungeeParty party = BungeePartyFactory.getPartyOf(p.getUniqueId());
                    if (party == null) {
                        BungeePartyFactory.createNewParty(p.getUniqueId());
                    }
                    this.loaded.add(p.getUniqueId());
                    p.sendMessage(BungeePartyFactory.PREFIX + "Your party has been loaded!");
                });
            }
        });
    }

    @EventSubscription
    private void onQuit(PlayerQuitEvent event) {

        //Removes member from party if needed
        //Or unloads/unregisters party to free memory
            BungeeParty party = BungeePartyFactory.getPartyOf(event.getPlayer().getUniqueId(), false);
            if (party != null) {
                if (!party.getLeader().equals(event.getPlayer().getUniqueId()) && Bukkit.getPlayer(party.getLeader()) != null) {
                    pl = event.getPlayer();
                    party.removeMember(event.getPlayer().getUniqueId());
                    pl = null;
                } else {
                    BungeePartyFactory.unregisterParty(party);
                }
            }
            this.loaded.remove(event.getPlayer().getUniqueId());
    }

    private static final String a = "";

    @EventSubscription
    private void onJoin(PlayerJoinEvent event) {

        //Loads or creates party for player
        Bukkit.getScheduler().runTaskLaterAsynchronously(CoreHandler.main, () -> {
            UUID id = event.getPlayer().getUniqueId();
            synchronized (BungeePartyFactory.class) {
                if(Bukkit.getPlayer(id) == null){
                    return;
                }
                BungeeParty party = BungeePartyFactory.getPartyOf(event.getPlayer().getUniqueId());

                if (party == null) BungeePartyFactory.createNewParty(event.getPlayer().getUniqueId());

                this.loaded.add(event.getPlayer().getUniqueId());
                event.getPlayer().sendMessage(BungeePartyFactory.PREFIX + "Your party has been loaded!");
            }
        }, 4);
    }

    public CompletableFuture<BungeeParty> getParty(UUID partyId) {
        CompletableFuture<BungeeParty> future = new CompletableFuture<>();
        if (!ProxyComm.instance.isEnabled()) {
            future.complete(null);
            return future;
        }
        ProxyComm.instance.register(new PartyMessageGet(partyId, future::complete));
        return future;
    }

    public CompletableFuture<BungeeParty> getPartyFromMember(UUID memberId) {
        CompletableFuture<BungeeParty> future = new CompletableFuture<>();
        if (!ProxyComm.instance.isEnabled()) {
            future.complete(null);
            return future;
        }
        ProxyComm.instance.register(new PartyMessageGetFromMember(memberId, future::complete));
        return future;
    }

    public void sendInvite(UUID partyId, UUID player, String inviter) {
        if (!ProxyComm.instance.isEnabled()) {
            return;
        }
        ProxyComm.instance.register(new PartyMessageInvite(partyId, player, inviter));
    }

    public void sendUnInvite(UUID partyId, UUID player) {
        if (!ProxyComm.instance.isEnabled()) {
            return;
        }
        ProxyComm.instance.register(new PartyMessageUnInvite(partyId, player));
    }

    public void sendAddMember(UUID partyId, UUID player) {
        if (!ProxyComm.instance.isEnabled()) {
            return;
        }
        ProxyComm.instance.register(new PartyMessageAddMember(partyId, player));
    }

    public void sendRemoveMember(UUID partyId, UUID player) {
        if (!ProxyComm.instance.isEnabled()) {
            return;
        }
        ProxyComm.instance.register(new PartyMessageRemoveMember(partyId, player));
    }

    public void sendSetLeader(UUID partyId, UUID leader) {
        if (!ProxyComm.instance.isEnabled()) {
            return;
        }
        ProxyComm.instance.register(new PartyMessageSetLeader(partyId, leader));
    }

    public void sendUpdateParty(BungeeParty party) {
        if (!ProxyComm.instance.isEnabled()) {
            return;
        }
        ProxyComm.instance.register(new PartyMessageUpdate(party));
    }


}
