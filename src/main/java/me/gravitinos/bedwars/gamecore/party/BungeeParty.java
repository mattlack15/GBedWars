package me.gravitinos.bedwars.gamecore.party;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BungeeParty extends BaseParty {
    public BungeeParty(@NotNull UUID leader) {
        super(leader);
    }

    @Override
    public void addMember(UUID member) {
        super.addMember(member);
        if(PartyComm.instance != null){
            PartyComm.instance.sendAddMember(this.getPartyId(), member);
        }
    }

    @Override
    public void setLeader(UUID leader) {
        super.setLeader(leader);
        if(PartyComm.instance != null){
            PartyComm.instance.sendSetLeader(this.getPartyId(), leader);
        }
    }

    public void setLeaderLocal(UUID leader) {
        super.setLeader(leader);
    }

    public void addMemberLocal(UUID member){
        super.addMember(member);
    }

    @Override
    public void invitePlayer(UUID id) {
        invitePlayer(id, "Unknown");
    }

    public void invitePlayer(UUID id, String inviter){
        super.invitePlayer(id);
        if(PartyComm.instance != null){
            PartyComm.instance.sendInvite(this.getPartyId(), id, inviter);
        }
    }

    public void invitePlayerLocal(UUID member){
        super.invitePlayer(member);
    }

    @Override
    public void removeMember(UUID member) {
        super.removeMember(member);
        if(PartyComm.instance != null){
            PartyComm.instance.sendRemoveMember(this.getPartyId(), member);
        }
    }

    public void removeMemberLocal(UUID member){
        super.removeMember(member);
    }

    @Override
    public void removeInvite(UUID player) {
        super.removeInvite(player);
        if(PartyComm.instance != null){
            PartyComm.instance.sendUnInvite(this.getPartyId(), player);
        }
    }

    public void removeInviteLocal(UUID player){
        super.removeInvite(player);
    }
}
