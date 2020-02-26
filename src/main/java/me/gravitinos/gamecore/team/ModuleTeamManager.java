package me.gravitinos.gamecore.team;

import me.gravitinos.gamecore.handler.GameHandler;
import me.gravitinos.gamecore.module.GameModule;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModuleTeamManager extends GameModule {

    private Map<UUID, String> teamIndex = new HashMap<>();

    public ModuleTeamManager(@NotNull GameHandler gameHandler) {
        super(gameHandler, "Team Manager");
    }

    /**
     * Set the team of a player
     * @param player The player to set the team for
     * @param teamName The team to set for the player
     */
    public void setTeam(@NotNull UUID player, @NotNull String teamName){
        this.teamIndex.put(player, teamName);
    }

    /**
     * Get the team of a player
     * @param player The player to get the team of
     * @return Either the team name of the team the player is on or null if the player is not on any team
     */
    public String getTeam(@NotNull UUID player){
        return this.teamIndex.get(player);
    }

    /**
     * Move everyone from one team onto another team
     * @param fromTeam The team to move players from
     * @param toTeam The team to move players into
     */
    public void moveFromTo(@NotNull String fromTeam, @NotNull String toTeam){
        this.teamIndex.replaceAll((p, t) -> fromTeam.equals(t) ? toTeam : t);
    }

    /**
     * Get the teams existing in this team manager
     * @return List of all the existing team names
     */
    public ArrayList<String> getTeams(){
        ArrayList<String> teams = new ArrayList<>();
        for(String team : teamIndex.values()){
            if(!teamIndex.values().contains(team)){
                teams.add(team);
            }
        }
        return teams;
    }
}
