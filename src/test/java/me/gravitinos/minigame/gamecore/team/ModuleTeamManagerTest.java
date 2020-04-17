package me.gravitinos.minigame.gamecore.team;

import me.gravitinos.minigame.gamecore.party.BaseParty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModuleTeamManagerTest {
    public static void main(String[] args){
        ModuleTeamManager teamManager = new ModuleTeamManager(null);

        ArrayList<BaseParty> parties = new ArrayList<>();

        String[] names = {"Greg", "Matthew", "Jack", "Jill", "Jared", "Nick", "Robert", "Martha", "Chris", "Ash", "Sebastian", "Carl", "Jayden", "Lucas", "Ryan", "Tristan"};

        Map<UUID, String> idNames = new HashMap<>();

        int n = 0;

        BaseParty party = new BaseParty(UUID.randomUUID());
        party.addMember(UUID.randomUUID());
        parties.add(party);

        for (UUID member : party.getMembers()) {
            idNames.put(member, names[n]);
            n++;
        }

        System.out.println("Party 1:");
        party.getMembers().forEach(m -> System.out.println(idNames.get(m)));
        System.out.println("\n");

        party = new BaseParty(UUID.randomUUID());
        party.addMember(UUID.randomUUID());
        parties.add(party);

        for (UUID member : party.getMembers()) {
            idNames.put(member, names[n]);
            n++;
        }

        System.out.println("Party 2:");
        party.getMembers().forEach(m -> System.out.println(idNames.get(m)));
        System.out.println("\n");

        party = new BaseParty(UUID.randomUUID());
        parties.add(party);

        for (UUID member : party.getMembers()) {
            idNames.put(member, names[n]);
            n++;
        }

        System.out.println("Party 3:");
        party.getMembers().forEach(m -> System.out.println(idNames.get(m)));
        System.out.println("\n");

        party = new BaseParty(UUID.randomUUID());
        parties.add(party);

        for (UUID member : party.getMembers()) {
            idNames.put(member, names[n]);
            n++;
        }

        System.out.println("Party 4:");
        party.getMembers().forEach(m -> System.out.println(idNames.get(m)));
        System.out.println("\n");

        teamManager.insertParties(parties, "RED", "BLUE", "GREEN", "YELLOW");

        for(String teams : teamManager.getTeams()){
            System.out.println("On " + teams + ":");
            teamManager.getPlayersOnTeam(teams).forEach(tm -> System.out.println(idNames.get(tm)));
            System.out.println("\n\n");
        }

    }
}