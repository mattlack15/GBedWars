package me.gravitinos.minigame.gamecore.party;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BungeePartyFactory {

    private static ArrayList<BungeeParty> parties = new ArrayList<>();

    public static final String PREFIX = ChatColor.GOLD + "Party > " + ChatColor.GRAY;

    /**
     * Create a new registered party
     * All parties with more than one member should be registered
     * @param leader The leader
     * @param members The members of the party
     * @return Party
     */
    public static BungeeParty createNewParty(@NotNull UUID leader, UUID... members){
        BungeeParty party = new BungeeParty(leader);
        for(UUID member : members){
            party.addMemberLocal(member);
        }

        registerParty(party);
        if(PartyComm.instance != null) {
            PartyComm.instance.sendUpdateParty(party);
        }
        return party;
    }

    public static BungeeParty createNewUnregisteredParty(@NotNull UUID leader, UUID... members){
        BungeeParty party = new BungeeParty(leader);
        for(UUID member : members){
            party.addMemberLocal(member);
        }
        return party;
    }

    public static void unregisterParty(BungeeParty party){
        parties.remove(party);
        party.reset();
    }

    public static BungeeParty createNewUnregisteredParty(@NotNull UUID leader, @NotNull UUID partyId, UUID... members){
        BungeeParty party = new BungeeParty(leader);
        party.setPartyId(partyId);
        party.setLeaderLocal(leader);
        for(UUID member : members){
            party.addMemberLocal(member);
        }
        return party;
    }

    public static void registerParty(BungeeParty party){
        if(!parties.contains(party)){
            parties.add(party);
        }
    }

    public static ArrayList<BungeeParty> getRegisteredParties() {
        return parties; //DO NOT change to Lists.newArrayList(parties)
    }

    /**
     * Get the party that a player is in
     * @param player The player to look for
     * @return The party of that player, null if none exists
     */
    public static BungeeParty getPartyOf(UUID player){
        return getPartyOf(player, true);
    }

    private static Map<UUID, String> syncObjects = new HashMap<>();

    private static String getSyncObject(UUID id){
        if(!syncObjects.containsKey(id)){
            syncObjects.put(id, "");
        }
        return syncObjects.get(id);

    }

    public static BungeeParty getPartyOf(UUID player, boolean askProxy){
        UUID pid = BaseParty.findParty(player);
        if(pid == null) {
            try {
                if(askProxy) {
                    synchronized (getSyncObject(player)) {
                        BungeeParty party = PartyComm.instance.getPartyFromMember(player).get(100, TimeUnit.MILLISECONDS);
                        if (!parties.contains(party) && party != null) {
                            parties.add(party);
                        }
                        return party;
                    }
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
               // e.printStackTrace();
            }
            return null;
        }
        return getRegisteredParty(pid, askProxy);
    }

    public static BungeeParty getRegisteredParty(@NotNull UUID partyId){
        return getRegisteredParty(partyId, true);
    }

    public static BungeeParty getRegisteredParty(@NotNull UUID partyId, boolean askProxy){
        for(BungeeParty party : parties){
            if(party.getPartyId().equals(partyId)){
                return party;
            }
        }
        if(!askProxy){
            return null;
        }
        synchronized (getSyncObject(partyId)) {
            try {
                BungeeParty party = PartyComm.instance.getParty(partyId).get(200, TimeUnit.MILLISECONDS);
                if (party != null) parties.add(party);
                return party;
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
