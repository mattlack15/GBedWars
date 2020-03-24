package me.gravitinos.bedwars.gamecore.scoreboard;

import com.google.common.collect.Lists;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.handler.GameHandler;
import me.gravitinos.bedwars.gamecore.module.GameModule;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ModuleScoreboard extends GameModule {
    private ArrayList<SBElement> elements = new ArrayList<>();
    private SBScope scope;
    private String title;
    private boolean enabled = false;
    private ArrayList<UUID> players = new ArrayList<>();
    private Map<UUID, Scoreboard> oldScoreboards = new HashMap<>();

    private ArrayList<Consumer<Player>> playerSpecificHandlers = new ArrayList<>();
    private ArrayList<Consumer<Scoreboard>> handlers = new ArrayList<>();

    private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    public ModuleScoreboard(GameHandler gameHandler, @NotNull String title, @NotNull SBScope scoreboardScope) {
        super(gameHandler, "Scoreboard");
        this.scope = scoreboardScope;

        if (this.scope.equals(SBScope.EVERYONE)) {
            Bukkit.getOnlinePlayers().forEach(p -> this.addPlayer(p.getUniqueId()));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!enabled) {
                    return;
                }
                for (UUID ids : players) {
                    Player player = Bukkit.getPlayer(ids);
                    if (player == null) {
                        continue;
                    }
                    Scoreboard board = player.getScoreboard();
                    if (board != scoreboard) {
                        oldScoreboards.put(ids, board);
                        player.setScoreboard(scoreboard);
                        board = scoreboard;
                    }

                    Objective objective = board.getObjective("sidebar");
                    if (objective == null) {
                        board.clearSlot(DisplaySlot.SIDEBAR);
                        objective = board.registerNewObjective("sidebar", "dummy");
                    }
                    if (objective.getDisplaySlot() != DisplaySlot.SIDEBAR) {
                        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    }

                    objective.setDisplayName(title);

                    ArrayList<String> foundValidElements = new ArrayList<>(); // To store the names of the elements on the scoreboard and to remove the lines on the scoreboard
                    // that don't have a corresponding element

                    int i = elements.size();
                    for (SBElement element : elements) {
                        Team team = board.getTeam(element.getName());
                        String text = ChatColor.translateAlternateColorCodes('&', element.getText());

                        String prefix;
                        String suffix = "";

                        //Process text
                        if (text.length() <= 16) {
                            //Is less than or is 16 chars long, only need prefix
                            prefix = text;
                        } else {
                            //More than 16 chars long, need suffix as well
                            if (text.charAt(15) == ChatColor.COLOR_CHAR) {
                                //If the char at 15 is a color char
                                prefix = text.substring(0, 15); //Include it in the suffix
                                suffix = text.substring(15);
                            } else {
                                //If the char at 15 is not a color char
                                prefix = text.substring(0, 16); //Include it in the prefix
                                suffix = text.substring(16);
                            }
                        }

                        if (suffix.length() > 16) {
                            suffix = suffix.substring(0, 16);
                        }

                        if (!ChatColor.getLastColors(element.getName()).equals(ChatColor.getLastColors(prefix)) && team != null && !ChatColor.getLastColors(prefix).equals("")) {
                            board.resetScores(team.getName()); //Remove team
                            team.unregister();
                            team = null;
                        }

                        if (team == null) {
                            element.setName(getNonExistingName(ChatColor.getLastColors(prefix)));
                            team = board.registerNewTeam(element.getName());
                        }

                        team.setPrefix(prefix);
                        team.setSuffix(suffix);

                        if (!team.hasEntry(element.getName())) {
                            team.addEntry(element.getName());
                            objective.getScore(element.getName()).setScore(i);
                        }

                        foundValidElements.add(element.getName());

                        i--;
                    }

                    Objective finalObjective = objective;

                    Scoreboard finalBoard = board;
                    Lists.newArrayList(board.getEntries()).forEach(entry -> { //Get rid of lines that aren't supposed to be there
                        if (finalObjective.getScore(entry).isScoreSet()) {
                            if (!foundValidElements.contains(entry)) {
                                finalBoard.resetScores(entry);
                                if (finalBoard.getTeam(entry) != null) {
                                    try {
                                        finalBoard.getTeam(entry).unregister();
                                    } catch (Exception ignored) {
                                    }
                                }
                            }
                        }
                    });

                    playerSpecificHandlers.forEach(h -> h.accept(player));
                }
                handlers.forEach(h -> h.accept(scoreboard));
            }
        }.runTaskTimer(CoreHandler.main, 0, 2);
    }

    public void addPlayerSpecificHandler(Consumer<Player> handler){
        this.playerSpecificHandlers.add(handler);
    }

    public void addHandler(Consumer<Scoreboard> handler){
        this.handlers.add(handler);
    }

    private String getNonExistingName(String lastColors) {
        boolean foundOne = false;
        String name = "";
        while (!foundOne) {
            name = SBElement.getRandom12CharName(lastColors);
            foundOne = true;
            for (SBElement element : elements) {
                if (element.getName().equals(name)) {
                    foundOne = false;
                }
            }
        }
        return name;
    }

    public Scoreboard getScoreboard(){
        return this.scoreboard;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public SBScope getScope() {
        return scope;
    }

    public void setEnabled(boolean enabled) {
        if (!enabled) {
            for (UUID ids : Lists.newArrayList(oldScoreboards.keySet())) {
                Player p = Bukkit.getPlayer(ids);
                if (p == null) {
                    oldScoreboards.remove(ids);
                    continue;
                }
                p.setScoreboard(oldScoreboards.get(ids));
                oldScoreboards.remove(ids);
            }
        }
        this.enabled = enabled;
    }

    /**
     * Sets the title for this scoreboard
     *
     * @param title The title to set to
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Add a player to this scoreboard
     *
     * @param id The UUID of the player to add
     */
    public void addPlayer(@NotNull UUID id) {
        this.players.add(id);
    }

    /**
     * Remove a player from this scoreboard
     *
     * @param id The UUID of the player to remove
     */
    public void removePlayer(@NotNull UUID id) {
        Player p = Bukkit.getPlayer(id);
        if (p != null && oldScoreboards.containsKey(id)) {
            p.setScoreboard(oldScoreboards.get(id));
        }
        oldScoreboards.remove(id);
        this.players.remove(id);
    }

    /**
     * Add an element to this scoreboard
     *
     * @param element The element to add
     * @return True if it was added, false if this scoreboard has reached the max elements possible (15)
     */
    public boolean addElement(@NotNull SBElement element) {
        if (elements.size() >= 15) {
            return false;
        }
        elements.add(element);
        return true;
    }

    @EventSubscription
    private void onJoin(PlayerJoinEvent event) {
        if (this.scope.equals(SBScope.EVERYONE)) {
            this.addPlayer(event.getPlayer().getUniqueId());
        }
    }

    @EventSubscription
    private void onLeave(PlayerQuitEvent event) {
        this.removePlayer(event.getPlayer().getUniqueId());
    }


}
