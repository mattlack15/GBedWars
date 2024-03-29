package me.gravitinos.minigame.bedwars.game.info;

import me.gravitinos.minigame.bedwars.game.BedwarsHandler;
import me.gravitinos.minigame.bedwars.game.BedwarsTeam;
import me.gravitinos.minigame.gamecore.util.Saving.SavedPlayerState;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BWPlayerInfo {

    private UUID uuid;
    private String name;

    private PermanentArmorType permanentArmorType = PermanentArmorType.LEATHER;
    private PermanentPickaxeType permanentPickaxeType = PermanentPickaxeType.NONE;

    private int kills = 0;
    private SavedPlayerState savedPlayerState = null;
    private int elimKills = 0;
    private int bedsDestroyed = 0;

    private boolean isRespawning = false;

    private BedwarsTeam lastTeam;

    private BedwarsHandler handler;

    public BWPlayerInfo(@NotNull BedwarsHandler handler, BedwarsTeam team, @NotNull UUID uuid, @NotNull String name){
        this.handler = handler;
        this.uuid = uuid;
        this.name = name;
        this.lastTeam = team;
    }

    public String getName() {
        return name;
    }

    public void setBedsDestroyed(int bedsDestroyed) {
        this.bedsDestroyed = bedsDestroyed;
    }

    public SavedPlayerState getSavedPlayerState() {
        return savedPlayerState;
    }

    public void setSavedPlayerState(SavedPlayerState savedPlayerState) {
        this.savedPlayerState = savedPlayerState;
    }

    public void setEliminationKills(int elimKills) {
        this.elimKills = elimKills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setRespawning(boolean respawning) {
        isRespawning = respawning;
    }

    public void setPermanentArmorType(PermanentArmorType permanentArmorType) {
        this.permanentArmorType = permanentArmorType;
    }

    public void setPermanentPickaxeType(PermanentPickaxeType permanentPickaxeType) {
        this.permanentPickaxeType = permanentPickaxeType;
    }

    public BedwarsHandler getHandler() {
        return handler;
    }

    public PermanentArmorType getPermanentArmorType() {
        return permanentArmorType;
    }

    public PermanentPickaxeType getPermanentPickaxeType() {
        return permanentPickaxeType;
    }

    public int getKills() {
        return kills;
    }

    public int getEliminationKills() {
        return elimKills;
    }

    public int getBedsDestroyed() {
        return bedsDestroyed;
    }

    public BWTeamInfo getTeamInfo(){
        return this.getHandler().getTeamInfo(this.getTeam());
    }

    /**
     * Returns a value representing whether this player is respawning or not
     * @return
     */
    public boolean isRespawning() {
        return isRespawning;
    }

    /**
     * Gets the UUID of this player
     * @return The UUID of this player
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Gets the team this player is on
     * @return The team
     */
    public BedwarsTeam getTeam(){
        BedwarsTeam team = BedwarsTeam.getTeam(handler.getTeamManagerModule().getTeam(uuid));
        if(team != null){
            this.lastTeam = team;
        }
        return this.lastTeam;
    }

    /**
     * Checks out data into SQL database, if one is available
     * Called on game end
     */
    public void sqlCheckout(){
        //TODO
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }
        if(o instanceof BWPlayerInfo){
            return ((BWPlayerInfo) o).uuid.equals(this.uuid);
        }
        return false;
    }
}
