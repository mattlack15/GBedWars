package me.gravitinos.bedwars.game.info;

import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.game.BedwarsTeam;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BWTeamInfo {

    private BedwarsTeam team;

    private Map<TeamUpgrade, Integer> teamUpgrades = new HashMap<>();

    private boolean bedDestroyed = false;

    public BWTeamInfo(@NotNull BedwarsHandler handler, @NotNull BedwarsTeam team){
        this.team = team;
    }

    public boolean isBedDestroyed() {
        return bedDestroyed;
    }

    public void setBedDestroyed(boolean bedDestroyed) {
        this.bedDestroyed = bedDestroyed;
    }

    public Map<TeamUpgrade, Integer> getTeamUpgrades() {
        return teamUpgrades;
    }

    public void addTeamUpdrade(TeamUpgrade upgrade, int level){
        if(level > upgrade.getMaxLevel()){
            level = upgrade.getMaxLevel();
        }

        this.teamUpgrades.put(upgrade, level);
    }

    public void removeTeamUpdate(TeamUpgrade upgrade){
        this.teamUpgrades.remove(upgrade);
    }

    /**
     * Gets the team this object contains info on
     * @return BedwarsTeam
     */
    public BedwarsTeam getTeam(){
        return this.team;
    }
}
