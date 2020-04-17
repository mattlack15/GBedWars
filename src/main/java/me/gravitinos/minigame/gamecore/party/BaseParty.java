package me.gravitinos.minigame.gamecore.party;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class BaseParty {
    private static final Map<UUID, UUID> memberMap = new HashMap<>();
    private static ArrayList<BiConsumer<UUID, UUID>> memberListeners = new ArrayList<>();
    
    private UUID partyId = UUID.randomUUID();
    private UUID leader = null;
    private Map<UUID, Long> invitedPlayers = new HashMap<>();

    public BaseParty(@NotNull UUID leader) {
        this.leader = leader;
        setMemberMapping(leader, this.getPartyId());
    }

    /**
     * Adds a listener for when a member's party is changed
     * @param consumer BiConsumer of member uuid and the id of the party that is about to be set as their party, either may be null
     */
    public static void addMemberMappingListener(BiConsumer<UUID, UUID> consumer){
        memberListeners.add(consumer);
    }

    public synchronized int getSize(){
        return this.getMembers().size();
    }

    /**
     * Get the PartyId of the party a member is mapped to
     * @param member The member
     */
    public static UUID findParty(UUID member){
        return memberMap.get(member);
    }
    
    private synchronized void setMemberMapping(UUID member, UUID mapping){
        memberListeners.forEach(l -> {
            try {
                l.accept(member, mapping);
            }catch(Exception e){
                e.printStackTrace();
            }
        });
        synchronized (memberMap) {
            memberMap.put(member, mapping);
        }
    }

    public void setPartyId(UUID partyId) {
        this.partyId = partyId;
        this.getMembers().forEach(m -> memberMap.put(m, partyId));
    }

    public void setLeader(UUID leader) {
        setMemberMapping(leader, this.getPartyId());
        this.leader = leader;
    }

    public void reset(){
        for(UUID members : this.getMembers()){
            memberMap.put(members, null);
        }
    }

    public Map<UUID, Long> getInvitedPlayers() {
        return invitedPlayers;
    }

    public UUID getPartyId() {
        return partyId;
    }

    public boolean isPlayerInvited(UUID player) {
        return this.invitedPlayers.containsKey(player);
    }

    /**
     * Gets the Leader of this party
     *
     * @return The UUID of the Leader of this party
     */
    public UUID getLeader() {
        if(this.getMembers().size() == 1){
            this.leader = this.getMembers().get(0);
        }
        return leader;
    }

    /**
     * Gets the System.currentTimeMillis() value at the time when the player was invited
     *
     * @param player The player
     * @return The time in milliseconds when the player was invited, or -1 if the player has not been invited at all
     */
    public long getInvitationTime(UUID player) {
        if (!this.isPlayerInvited(player)) {
            return -1;
        }
        return this.invitedPlayers.get(player);
    }

    public void removeInvite(UUID player) {
        this.invitedPlayers.remove(player);
    }

    /**
     * Adds a player to the invited list
     *
     * @param id The uuid of the player
     */
    public void invitePlayer(UUID id) {
        this.invitedPlayers.put(id, System.currentTimeMillis());
    }

    public boolean isOnePersonParty() {
        return this.getMembers().size() == 1;
    }

    /**
     * Add a member to the party
     *
     * @param member The member to add
     */
    public void addMember(UUID member) {
        setMemberMapping(member, this.getPartyId());
    }

    /**
     * Get the members of the party
     *
     * @return Members
     */
    public ArrayList<UUID> getMembers() {
        ArrayList<UUID> ids = new ArrayList<>();
        synchronized (memberMap) {
            memberMap.forEach((a, b) -> {
                if (this.getPartyId().equals(b)) {
                    ids.add(a);
                }
            });
        }
        return ids;
    }

    /**
     * Remove a member from the party. If it is the leader, a new leader will be chosen
     *
     * @param member The member to remove
     */
    public void removeMember(UUID member) {
        setMemberMapping(member, null);
        if(leader.equals(member)){
            this.chooseNewLeader();
        }
    }

    private void chooseNewLeader() {
        if (getMembers().size() < 1) {
            return;
        }
        this.setLeader(getMembers().get(0));
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BaseParty) {
            return ((BaseParty) object).getPartyId().equals(this.getPartyId());
        }
        return false;
    }
}
