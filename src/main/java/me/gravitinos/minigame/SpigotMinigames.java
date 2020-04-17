package me.gravitinos.minigame;

import me.gravitinos.minigame.diagnostic.Diagnostic;
import me.gravitinos.minigame.diagnostic.DiagnosticBukkitYml;
import me.gravitinos.minigame.diagnostic.DiagnosticNoLobbySpawn;
import me.gravitinos.minigame.gamecore.CoreHandler;
import me.gravitinos.minigame.gamecore.GameServerManager;
import me.gravitinos.minigame.gamecore.data.MiniPlayer;
import me.gravitinos.minigame.gamecore.module.GameStopReason;
import me.gravitinos.minigame.gamecore.party.BungeeParty;
import me.gravitinos.minigame.gamecore.party.BungeePartyFactory;
import me.gravitinos.minigame.gamecore.util.EventSubscription;
import me.gravitinos.minigame.gamecore.util.EventSubscriptions;
import me.gravitinos.minigame.gamecore.util.OnDisable;
import net.royawesome.jlibnoise.module.combiner.Min;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class SpigotMinigames extends JavaPlugin {
    public static SpigotMinigames instance;

    public static EnumMinigame minigame;

    public static GameServerManager manager;

    private boolean restartNeeded = false;

    private ArrayList<String> diagnosticMessages = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;

        minigame = EnumMinigame.Bedwars;

        this.saveDefaultConfig();

        new CoreHandler(this);

        this.diagnostics(new DiagnosticNoLobbySpawn(), new DiagnosticBukkitYml());

        try {
            manager = enableMinigame(minigame);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to enable " + minigame + ", the manager class does not have a no-args constructor");
            Bukkit.getOnlinePlayers().forEach(p -> MinigameMessenger.msgPlayerFatal(p, "Failed to enable " + minigame + ", the manager class does not have a no-args constructor"));
        }

        //Lobby setup
        setupAndEnableLobby(manager);

        EventSubscriptions.instance.subscribe(this);
    }

    public void setRestartNeeded(boolean bool){
        this.restartNeeded = bool;
        if(bool){
            Bukkit.getOnlinePlayers().forEach(p -> {
                if(p.hasPermission("game.viewdiagnostics")){
                    MinigameMessenger.msgPlayerWarning(p, "Restart needed!");
                }
            });
        }
    }

    @Override
    public void onDisable() {
        EventSubscriptions.instance.onDisable();
        if (manager.getLobby().getGame().isRunning())
            manager.getLobby().getGame().stop("Server stopping", GameStopReason.GAME_END);

        manager.onDisable();
    }

    /**
     * Setup and enable lobby for a game server manager
     */
    public void setupAndEnableLobby(GameServerManager manager) {
        Bukkit.getOnlinePlayers().forEach(p -> manager.getLobby().addPlayer(p.getUniqueId()));
        manager.getLobby().enable();
    }

    /**
     * Runs diagnostics on the server's setup
     * Messages online staff and console about any warnings/errors found
     */
    public void diagnostics(Diagnostic... diagnostics) {

        //Loop through all diagnostics
        for (Diagnostic diagnostic : diagnostics) {
            if (diagnostic.runDiagnostic()) {

                //Warn console
                Bukkit.getLogger().warning(diagnostic.getMessage());

                //Send message to online staff
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (player.hasPermission("game.viewdiagnostics")) {
                        MinigameMessenger.msgPlayerWarning(player, diagnostic.getMessage());
                    }
                });

                if (diagnostic.isFixable()) {
                    if (diagnostic.fix()) {
                        //Warn console
                        Bukkit.getLogger().warning("Problem was automatically fixed!");

                        //Send message to online staff
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            if (player.hasPermission("game.viewdiagnostics")) {
                                MinigameMessenger.msgPlayerWarning(player, "&aProblem was automatically fixed!");
                            }
                        });
                    } else {
                        //Add the message to saved diagnostic messages
                        this.diagnosticMessages.add(diagnostic.getMessage());

                        //Warn console
                        Bukkit.getLogger().warning("Problem failed to be automatically fixed!");

                        //Send message to online staff
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            if (player.hasPermission("game.viewdiagnostics")) {
                                MinigameMessenger.msgPlayerWarning(player, "&cProblem failed to be automatically fixed!");
                            }
                        });
                    }
                } else {
                    //Add the message to saved diagnostic messages
                    this.diagnosticMessages.add(diagnostic.getMessage());
                }

                diagnostic.actions();
            }
        }
    }

    /**
     * Get an instance of a minigame and enable it
     *
     * @param minigame The minigame type
     */
    public GameServerManager<?> enableMinigame(EnumMinigame minigame) throws IllegalAccessException, InstantiationException {
        Class<? extends GameServerManager> clazz = minigame.getManagerClass();
        GameServerManager manager = clazz.newInstance();
        manager.onEnable();
        return manager;
    }

    @EventSubscription
    private void onPreJoin(AsyncPlayerPreLoginEvent event) {
        MiniPlayer.getPlayer(event.getUniqueId(), event.getName());
        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            BungeePartyFactory.getPartyOf(event.getUniqueId());
        }
    }

    @EventSubscription
    private void onJoin(PlayerJoinEvent event) {
        CoreHandler.instance.getAsyncExecutor().execute(() -> {
            if ((Bukkit.getOnlinePlayers().contains(event.getPlayer()) && Bukkit.getOnlinePlayers().size() == 1) ||
                    !Bukkit.getOnlinePlayers().contains(event.getPlayer()) && Bukkit.getOnlinePlayers().size() == 0) {
                BungeePartyFactory.getPartyOf(event.getPlayer().getUniqueId());
            }
            CoreHandler.doInMainThread(() -> manager.getLobby().addPlayer(event.getPlayer().getUniqueId()));
        });

        UUID id = event.getPlayer().getUniqueId();
        if(diagnosticMessages.size() > 0 || restartNeeded) {
            if (event.getPlayer().hasPermission("game.viewdiagnostics")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Player player = Bukkit.getPlayer(id);
                        if (player != null) {
                            diagnosticMessages.forEach(m -> MinigameMessenger.msgPlayerWarning(player, m));
                            MinigameMessenger.msgPlayerWarning(player, "Restart needed!");
                        }
                    }
                }.runTaskLater(instance, 40);
            }
        }
    }

    @EventSubscription
    private void onLeave(PlayerQuitEvent event) {

        manager.getLobby().getGame().kickPlayer(event.getPlayer().getUniqueId());

        //Removes member from party if needed
        //Or unloads/unregisters party to free memory
        BungeeParty party = BungeePartyFactory.getPartyOf(event.getPlayer().getUniqueId(), false);
        if (party != null) {
            if (!party.getLeader().equals(event.getPlayer().getUniqueId()) && Bukkit.getPlayer(party.getLeader()) != null) {
                party.removeMember(event.getPlayer().getUniqueId());
            } else {
                BungeePartyFactory.unregisterParty(party);
            }
        }

        manager.getLobby().removePlayer(event.getPlayer().getUniqueId());
    }

    @OnDisable
    private void onServerDisable() {
    }

}
