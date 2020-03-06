package me.gravitinos.bedwars.gamecore.team;

import com.google.common.collect.Lists;
import me.gravitinos.bedwars.gamecore.handler.GameHandler;
import me.gravitinos.bedwars.gamecore.module.GameModule;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ModuleTeamManager extends GameModule {

    private Map<UUID, String> teamIndex = new HashMap<>();

    public ModuleTeamManager(@NotNull GameHandler gameHandler) {
        super(gameHandler, "Team Manager");
    }

    /**
     * Set the team of a player
     *
     * @param player   The player to set the team for
     * @param teamName The team to set for the player
     */
    public void setTeam(@NotNull UUID player, @NotNull String teamName) {
        this.teamIndex.put(player, teamName);
    }

    /**
     * Get the team of a player
     *
     * @param player The player to get the team of
     * @return Either the team name of the team the player is on or null if the player is not on any team
     */
    public String getTeam(@NotNull UUID player) {
        return this.teamIndex.get(player);
    }

    /**
     * Clears this of all teams and players
     */
    public void clear() {
        this.teamIndex.clear();
    }

    /**
     * Removes a team, and removes all players on that team
     *
     * @param team The team to remove
     */
    public void removeTeam(@NotNull String team) {
        this.getPlayersOnTeam(team).forEach(this::removePlayer);
    }

    /**
     * Removes a player
     *
     * @param player The player to remove
     */
    public void removePlayer(UUID player) {
        this.teamIndex.remove(player);
    }

    /**
     * Move everyone from one team onto another team
     *
     * @param fromTeam The team to move players from
     * @param toTeam   The team to move players into
     */
    public void moveFromTo(@NotNull String fromTeam, @NotNull String toTeam) {
        this.teamIndex.replaceAll((p, t) -> fromTeam.equals(t) ? toTeam : t);
    }

    /**
     * Inserts a set of players into teams of some size
     *
     * @param players  The players
     * @param teamSize The team size
     */
    public void insert(@NotNull ArrayList<UUID> players, int teamSize) {
        if (teamSize < 1) {
            teamSize = 1;
        }

        Collections.shuffle(players);
        int currentTeam = 1;
        for (UUID player : players) {
            String team = currentTeam + "";

            this.setTeam(player, team);

            if (this.getPlayersOnTeam(team).size() >= teamSize) {
                currentTeam++;
            }
        }
    }

    /**
     * Inserts a set of players equally into teams of the names specified
     *
     * @param players The players
     * @param teams   The team names
     */
    public void insert(@NotNull ArrayList<UUID> players, String... teams) {

        Collections.shuffle(players);

        if(teams.length == 0){
            return;
        }

        int currentTeam = 0;
        for (UUID player : players) {

            if (currentTeam >= teams.length)
                currentTeam = 0;

            String team = teams[currentTeam];
            this.setTeam(player, team);

            currentTeam++;
        }
    }

    /**
     * Check if a player is contained in this team manager
     * @param player The player to check
     * @return The answer
     */
    public boolean isContained(UUID player){
        return this.teamIndex.containsKey(player);
    }

    /**
     * Get the teams existing in this team manager
     *
     * @return List of all the existing team names
     */
    public ArrayList<String> getTeams() {
        ArrayList<String> teams = new ArrayList<>();
        for (String team : teamIndex.values()) {
            if (!teams.contains(team)) {
                teams.add(team);
            }
        }
        return teams;
    }

    /**
     * Get the players in this team manager
     * @return List of the players in this team manager
     */
    public ArrayList<UUID> getPlayers(){
        return Lists.newArrayList(this.teamIndex.keySet());
    }

    /**
     * Gets all the players on a team
     * @param team The team
     * @return All the players on that team
     */
    public ArrayList<UUID> getPlayersOnTeam(String team) {
        ArrayList<UUID> players = new ArrayList<>();
        this.teamIndex.forEach((u, t) -> {
            if (t.equals(team)) {
                players.add(u);
            }
        });
        return players;
    }
}
