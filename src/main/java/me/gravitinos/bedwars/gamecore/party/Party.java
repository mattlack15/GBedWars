package me.gravitinos.bedwars.gamecore.party;

public class Party {
    private ArrayList<UUID> members = new ArrayList<>();
    private UUID leader = null;

    public Party(@NotNull UUID leader){
        this.leader = members;
    }

    public boolean isOnePersonParty(){
        return this.members.size == 1;
    }

    public void addMember(UUID member){
        if(!this.members.contains(member)) {
            this.members.add(member);
        }
    }

    public void removeMember(UUID member){
        members.remove(member);
        if(leader.equals(member)){
            this.leader = null;
            this.chooseNewLeader();
        }
    }

    private void chooseNewLeader(){
        if(members.size() < 1){
            return;
        }
        this.leader = members.get(0);
    }
}
