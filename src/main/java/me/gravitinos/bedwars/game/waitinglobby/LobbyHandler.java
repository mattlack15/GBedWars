package me.gravitinos.bedwars.game.waitinglobby;

import me.gravitinos.bedwars.gamecore.handler.GameHandler;
import me.gravitinos.bedwars.gamecore.handler.GameStopReason;
import me.gravitinos.bedwars.gamecore.party.BaseParty;
import me.gravitinos.bedwars.gamecore.scoreboard.ModuleScoreboard;
import me.gravitinos.bedwars.gamecore.scoreboard.SBScope;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This class handles the server while it is in the waiting lobby state
 */
public class LobbyHandler extends GameHandler {
    public LobbyHandler() {
        super("Waiting Lobby", -1);

        //Modules

            //Scoreboard
        this.addModule(new ModuleScoreboard(this, "&c&lSoraxus Bedwars", SBScope.EVERYONE));
    }

    @Override
    public CompletableFuture<Boolean> start(ArrayList<BaseParty> players) {



        CompletableFuture<Boolean> future = new CompletableFuture<>();
        future.complete(true);
        return future;
    }

    @Override
    public CompletableFuture<Void> stop(String stopMessage, GameStopReason reason) {
        return null;
    }

    @Override
    public void kickPlayer(UUID player) {

    }

    @Override
    public boolean addPlayer(UUID player) {
        return false;
    }

    @Override
    public boolean addSpectator(UUID player) {
        return false;
    }

    @Override
    public void kickSpectator(UUID player) {

    }

    @Override
    public boolean isPlaying(UUID player) {
        return false;
    }

    @Override
    public boolean isSpectating(UUID player) {
        return false;
    }

    @Override
    public ArrayList<UUID> getPlayers() {
        return null;
    }

    @Override
    public ArrayList<UUID> getSpectators() {
        return null;
    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
