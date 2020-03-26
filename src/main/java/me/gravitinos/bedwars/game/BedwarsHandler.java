package me.gravitinos.bedwars.game;

import com.boydti.fawe.FaweAPI;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.gravitinos.bedwars.game.info.BWPlayerInfo;
import me.gravitinos.bedwars.game.info.BWTeamInfo;
import me.gravitinos.bedwars.game.module.*;
import me.gravitinos.bedwars.game.module.damage.DeathType;
import me.gravitinos.bedwars.game.module.damage.LastDamageList;
import me.gravitinos.bedwars.game.module.gameitems.*;
import me.gravitinos.bedwars.game.module.shop.PlayerDependantShop;
import me.gravitinos.bedwars.game.module.shop.Shop;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.gameitem.GameItemHandler;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.bedwars.gamecore.module.GameHandler;
import me.gravitinos.bedwars.gamecore.module.GameStopReason;
import me.gravitinos.bedwars.gamecore.map.ModuleBorder;
import me.gravitinos.bedwars.gamecore.party.BaseParty;
import me.gravitinos.bedwars.gamecore.scoreboard.ModuleScoreboard;
import me.gravitinos.bedwars.gamecore.scoreboard.SBElement;
import me.gravitinos.bedwars.gamecore.scoreboard.SBScope;
import me.gravitinos.bedwars.gamecore.team.ModuleTeamManager;
import me.gravitinos.bedwars.gamecore.util.ActionBar;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import me.gravitinos.bedwars.gamecore.util.HideUtil;
import me.gravitinos.bedwars.gamecore.util.InstantFirework;
import me.gravitinos.bedwars.gamecore.util.Saving.SavedPlayerState;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class BedwarsHandler extends GameHandler {

    private ArrayList<BWTeamInfo> teamInfo = new ArrayList<>();
    private ArrayList<BWPlayerInfo> playerInfo = new ArrayList<>();

    //private Map<UUID, SavedPlayerState> playerStates = new HashMap<>();

    //private ArrayList<UUID> respawning = new ArrayList<>();
    private ArrayList<UUID> spectating = new ArrayList<>();

    private static final double RESPAWN_TIME_SECONDS = 6;

    //private ArrayList<BedwarsTeam> existingBeds = new ArrayList<>();

    private boolean running = false;
    private BedwarsMapPointTracker pointTracker;

    private CuboidRegion mapRegion;

    private File map;

    public BedwarsHandler(File map) {
        super("Bedwars", 3600);
        this.map = map;
    }

    /**
     * ! This function will not work if the game is running
     * @param map The map file
     */
    public void setMap(File map){
        if(this.isRunning()) return;
        this.map = map;
    }

    private boolean setupModules(File map){
        try {
            this.clearModules();

            new BedwarsMapDataHandler(map);
            this.pointTracker = new BedwarsMapPointTracker(BedwarsMapDataHandler.instance);


            //--- Team Info Setup ---
            for (BedwarsTeam teams : BedwarsTeam.values()) {
                teamInfo.add(new BWTeamInfo(this, teams));
            }

            //--- Game Item Setup ---
            this.addModule(new ModuleGameItems(this));
            ModuleGameItems gameItems = this.getModule(ModuleGameItems.class);
            gameItems.addGameItemHandler(new ItemResourceIron(gameItems));
            gameItems.addGameItemHandler(new ItemResourceGold(gameItems));
            gameItems.addGameItemHandler(new ItemResourceDiamond(gameItems));
            gameItems.addGameItemHandler(new ItemResourceEmerald(gameItems));
            gameItems.addGameItemHandler(new ItemEnderpearl(gameItems));
            gameItems.addGameItemHandler(new ItemSpaceEnderpearl(gameItems));
            gameItems.addGameItemHandler(new ItemInvisPot(gameItems));
            gameItems.addGameItemHandler(new ItemExplosiveBow(gameItems));
            gameItems.addGameItemHandler(new ItemTnt(gameItems));
            gameItems.addGameItemHandler(new ItemBridgeEgg(gameItems));
            gameItems.addGameItemHandler(new ItemDefectiveEnderpearl(gameItems));



            this.addModule(new ModuleTeamManager(this));
            this.addModule(new ModuleScoreboard(this, ChatColor.RED + "Soraxus BedWars", SBScope.EVERYONE));
            this.addModule(new ModuleDamageHandler(this));
            this.addModule(new ModuleTeamUpgrades(this));
            this.addModule(new ModuleCoolDeaths(this));
            this.addModule(new ModulePvp(this));

            //--- Scoreboard Setup ---
            this.getModule(ModuleScoreboard.class).addElement(new SBElement(""));

            this.getModule(ModuleScoreboard.class).addElement(new SBElement("")
                    .setTextGetter(() -> {
                        int teamSize = getTeamManagerModule().getPlayersOnTeam(BedwarsTeam.BLUE.toString()).size();
                        return "&b&lBlue " + (!getTeamInfo(BedwarsTeam.BLUE).isBedDestroyed() ? "&a✔" : (teamSize == 0 ? "&c✘" : "&f" + teamSize));
                    }));

            this.getModule(ModuleScoreboard.class).addElement(new SBElement("")
                    .setTextGetter(() -> {
                        int teamSize = getTeamManagerModule().getPlayersOnTeam(BedwarsTeam.RED.toString()).size();
                        return "&c&lRed " + (!getTeamInfo(BedwarsTeam.RED).isBedDestroyed() ? "&a✔" : (teamSize == 0 ? "&c✘" : "&f" + teamSize));
                    }));

            this.getModule(ModuleScoreboard.class).addElement(new SBElement("")
                    .setTextGetter(() -> {
                        int teamSize = getTeamManagerModule().getPlayersOnTeam(BedwarsTeam.GREEN.toString()).size();
                        return "&a&lGreen " + (!getTeamInfo(BedwarsTeam.GREEN).isBedDestroyed() ? "&a✔" : (teamSize == 0 ? "&c✘" : "&f" + teamSize));
                    }));

            this.getModule(ModuleScoreboard.class).addElement(new SBElement("")
                    .setTextGetter(() -> {
                        int teamSize = getTeamManagerModule().getPlayersOnTeam(BedwarsTeam.YELLOW.toString()).size();
                        return "&e&lYellow " + (!getTeamInfo(BedwarsTeam.YELLOW).isBedDestroyed() ? "&a✔" : (teamSize == 0 ? "&c✘" : "&f" + teamSize));
                    }));

            //Scoreboard Team Colours (Name colours)
            this.getModule(ModuleScoreboard.class).addHandler((sb) -> {
                ItemInvisPot invisPotHandler = getModule(ModuleGameItems.class).getGameItem(ItemInvisPot.class);
                for (BedwarsTeam team : BedwarsTeam.values()) {
                    Team sbTeam = sb.getTeam(team.getName());
                    if (sbTeam == null) {
                        sbTeam = sb.registerNewTeam(team.getName());
                        sbTeam.setPrefix(team.getChatColour() + "");
                    }

                    Team finalSbTeam = sbTeam;
                    getTeamManagerModule().getPlayersOnTeam(team.getName()).forEach(tm -> {
                        Player player = Bukkit.getPlayer(tm);
                        if (player == null) return;
                        player.setPlayerListName(team.getChatColour() + player.getName());
                        if (!finalSbTeam.hasEntry(player.getName()) && !invisPotHandler.getInvisiblePlayers().contains(player.getUniqueId())) {
                            finalSbTeam.addEntry(player.getName());
                        } else if(finalSbTeam.hasEntry(player.getName()) && invisPotHandler.getInvisiblePlayers().contains(player.getUniqueId())){
                            finalSbTeam.removeEntry(player.getName());
                        }
                    });
                }
            });

            //Generators
            this.addModule(new ModuleGenerators(this, pointTracker.getMidGens(), pointTracker.getOuterGens(), pointTracker.getBaseGens()));

            //Player setup
            this.addModule(new ModulePlayerSetup(this));

            //--- Shop Setup ---
            ArrayList<Shop> shops = new ArrayList<>();
            for (BedwarsTeam team : BedwarsTeam.values()) {
                shops.add(new PlayerDependantShop(this, "&e&lShop", pointTracker.getShop(team), this.getGameItemsModule()));
            }

            this.addModule(new ModuleShops(this, shops));

            //Get map region
            Location b1 = this.pointTracker.getBorder1();
            Location b2 = this.pointTracker.getBorder2();

            this.mapRegion = new CuboidRegion(FaweAPI.getWorld(b1.getWorld().getName()), new com.sk89q.worldedit.Vector(b1.getX(), b1.getY(), b1.getZ()), new com.sk89q.worldedit.Vector(b2.getX(), b2.getY(), b2.getZ()));

            this.addModule(new ModuleGameEnvironment(this, mapRegion));
            this.addModule(new ModuleBorder(this, mapRegion, (p) -> {
                if (this.isSpectating(p.getUniqueId())) {
                    ArrayList<Location> midGens = this.getPointTracker().getMidGens();
                    if (midGens.size() > 0) {
                        p.teleport(midGens.get(0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    } else {
                        p.teleport(new Location(Bukkit.getWorld(Objects.requireNonNull(mapRegion.getWorld()).getName()), mapRegion.getCenter().getX(), mapRegion.getCenter().getY(), mapRegion.getCenter().getZ()), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    }
                } else if (this.isPlaying(p.getUniqueId())) {
                    LastDamageList lastDamageList = getModule(ModuleDamageHandler.class).getLastDamageList(p.getUniqueId());
                    DeathType deathType = DeathType.BORDER;
                    String by = "Border";
                    if (lastDamageList.getLastDamages().size() > 0) {
                        deathType = DeathType.KNOCKED_OFF;
                        by = lastDamageList.getLastDamages().get(0).getDamager();
                    }

                    if (Bukkit.getPlayer(by) != null) {
                        this.killPlayer(p.getUniqueId(), deathType, by, Bukkit.getPlayer(by));
                    } else {
                        this.killPlayer(p.getUniqueId(), deathType, by);
                    }
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
            this.stop("Game Error", GameStopReason.GAME_ERROR);
            return false;
        }
        return true;
    }

    public BWTeamInfo getTeamInfo(BedwarsTeam team) {
        for (BWTeamInfo infos : teamInfo) {
            if (infos.getTeam().equals(team)) {
                return infos;
            }
        }
        return null;
    }

    public BWPlayerInfo getPlayerInfo(UUID player) {
        for (BWPlayerInfo infos : playerInfo) {
            if (infos.getUuid().equals(player)) {
                return infos;
            }
        }
        return null;
    }

    public GameItemHandler getGameItem(String name) {
        return this.getGameItemsModule().getGameItem(name);
    }

    public BedwarsMapPointTracker getPointTracker() {
        return pointTracker;
    }

    public CuboidRegion getMapRegion() {
        return this.mapRegion;
    }

    public ModuleGameItems getGameItemsModule() {
        return this.getModule(ModuleGameItems.class);
    }

    public ModuleTeamManager getTeamManagerModule() {
        return this.getModule(ModuleTeamManager.class);
    }

    public ModuleGameEnvironment getBlockHandlerModule() {
        return this.getModule(ModuleGameEnvironment.class);
    }

    @Override
    public CompletableFuture<Boolean> start(ArrayList<BaseParty> players) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        CoreHandler.doInMainThread(() -> {

            if(!this.setupModules(map)){
                return;
            }

            this.running = true;

            //Startup
            try {
                this.playerInfo.clear();

                //Team info
                this.teamInfo.clear();
                for (BedwarsTeam teams : BedwarsTeam.values()) {
                    teamInfo.add(new BWTeamInfo(this, teams));
                }

                this.getModule(ModuleGenerators.class).setup();
                this.getGameItemsModule().enableAllGameItems();

                this.enableAllModules();

                this.getTeamManagerModule().clear();
                ArrayList<String> teams = new ArrayList<>();
                Lists.newArrayList(BedwarsTeam.values()).forEach(t -> teams.add(t.toString()));
                this.getTeamManagerModule().insertParties(players, BedwarsTeam.BLUE.getName(), BedwarsTeam.GREEN.getName(), BedwarsTeam.RED.getName(), BedwarsTeam.YELLOW.getName());

                Map<BedwarsTeam, Integer> si = new HashMap<>();
                for (BedwarsTeam team : BedwarsTeam.values()) {
                    si.put(team, 0);
                }

                Bukkit.getWorld(Objects.requireNonNull(mapRegion.getWorld()).getName()).setTime(5000);

                for (UUID uuid : this.getTeamManagerModule().getPlayers()) {

                    BedwarsTeam team = BedwarsTeam.getTeam(this.getTeamManagerModule().getTeam(uuid));
                    ArrayList<Location> spawnpoints = this.getPointTracker().getSpawnpoints(team);

                    int num = si.get(team);
                    if (num >= spawnpoints.size()) {
                        num = 0;
                        si.put(team, 0);
                    }

                    this.addPlayer(uuid, spawnpoints.get(num), team);
                    si.put(team, ++num);
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.stop("Error", GameStopReason.GAME_ERROR);
            }
            future.complete(true);
        });
        return future;
    }

    @Override
    public CompletableFuture<Void> stop(String stopMessage, GameStopReason reason) {

        this.running = false;

        if(stopMessage.length() > 0) {
            sendServerMessage("Game ended, " + ChatColor.translateAlternateColorCodes('&', stopMessage), "Game");
        }

        this.getGameItemsModule().disableAllGameItems();
        this.getModule(ModuleGameEnvironment.class).revertBlocks();

        this.internalEntityCleanup();

        for (UUID players : Lists.newArrayList(this.getTeamManagerModule().getPlayers())) {
            this.kickPlayer(players);
        }

        for (UUID spectators : Lists.newArrayList(this.spectating)) {
            this.kickSpectator(spectators);
        }

        this.disableAllModules();

        return null;
    }

    /**
     * Kill a player
     * @param player The player to kill
     * @param deathType The type of death
     * @param killedBy Who/What the player was killed by
     */
    public void killPlayer(@NotNull UUID player, @NotNull DeathType deathType, @NotNull String killedBy) {
        this.killPlayer(player, deathType, killedBy, null);
    }

    /**
     * Kill a player
     * @param player The player to kill
     * @param deathType The type of death
     * @param killedBy Who/What the player was killed by
     * @param giveItemsTo If anyone, who to give the player's items to (may be null)
     */
    public void killPlayer(@NotNull UUID player, @NotNull DeathType deathType, @NotNull String killedBy, Player giveItemsTo) {
        Player p = Bukkit.getPlayer(player);

        killedBy = ChatColor.translateAlternateColorCodes('&', killedBy);

        BedwarsTeam team = BedwarsTeam.getTeam(getTeamManagerModule().getTeam(player));

        if (!this.getPlayerInfo(player).isRespawning()) {
            this.getModule(ModuleCoolDeaths.class).bloodDeath(p);
        }

        this.getModule(ModulePlayerSetup.class).killPlayer(p, this.getTeamInfo(team).isBedDestroyed(), giveItemsTo);
        this.getModule(ModuleGameItems.class).getGameItem(ItemInvisPot.class).removeInvisible(p);

        if (this.getPlayerInfo(player).isRespawning()) {
            return;
        }

        boolean elimination = this.getTeamInfo(team).isBedDestroyed();

        this.sendGameMessage(getPlayerInfo(p.getUniqueId()).getTeam().getChatColour() + p.getName() + ChatColor.GRAY + " has been " + deathType.getDeathMessage() + " by " + (Bukkit.getPlayer(ChatColor.stripColor(killedBy)) != null ? getPlayerInfo(Bukkit.getPlayer(ChatColor.stripColor(killedBy)).getUniqueId()).getTeam().getChatColour() : ChatColor.YELLOW) + killedBy + ChatColor.AQUA + ChatColor.BOLD + (elimination ? " ELIMINATION" : ""), "Death");

        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT, 10f, 1f);

        //Register a kill
        if (Bukkit.getPlayer(ChatColor.stripColor(killedBy)) != null) {
            Player dgr = Bukkit.getPlayer(ChatColor.stripColor(killedBy));
            BWPlayerInfo pInfo = getPlayerInfo(dgr.getUniqueId());

            if (elimination) {
                pInfo.setEliminationKills(pInfo.getEliminationKills() + 1);
            }
            pInfo.setKills(pInfo.getKills() + 1);
        }

        if (elimination) {
            this.kickPlayer(player);
            return;
        }

        p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "You Died!", "You will respawn in " + Math.ceil(RESPAWN_TIME_SECONDS) + " seconds");
        p.setAllowFlight(true);
        p.setFlying(true);

        this.getPlayerInfo(player).setRespawning(true);

        //RESPAWN TASk
        new BukkitRunnable() {

            private int counter = (int) Math.ceil(RESPAWN_TIME_SECONDS * 10);

            @Override
            public void run() {

                //If they are not playing anymore, or never were, don't respawn
                if (!isPlaying(player)) {
                    Bukkit.getScheduler().cancelTask(this.getTaskId());
                    getPlayerInfo(player).setRespawning(false);
                    return;
                }

                //Send action bar to player about to be respawned
                ActionBar.send(Bukkit.getPlayer(player), "&eRespawning in &f" + (counter / 10d));

                if (--counter <= 0) {
                    Bukkit.getScheduler().cancelTask(this.getTaskId());

                    getPlayerInfo(player).setRespawning(false);

                    //Respawn player
                    ArrayList<Location> spawns = getPointTracker().getSpawnpoints(BedwarsTeam.getTeam(getTeamManagerModule().getTeam(player)));
                    if (spawns == null || spawns.size() < 1) {
                        Bukkit.getPlayer(player).sendMessage("Could not find a spawn to teleport you to!");
                        return;
                    }
                    ActionBar.send(Bukkit.getPlayer(player), ChatColor.GREEN + "Respawned!");
                    getModule(ModulePlayerSetup.class).spawnPlayer(Bukkit.getPlayer(player), spawns.get(new Random(System.currentTimeMillis()).nextInt(spawns.size())), BedwarsPlayer.getPlayer(player).getKit(), BedwarsTeam.getTeam(getTeamManagerModule().getTeam(player)));
                }
            }
        }.runTaskTimer(CoreHandler.main, 0, 2);
    }

    @Override
    public void kickPlayer(UUID player) {
        this.removePlayingPlayer(player, true);
    }

    private void removePlayingPlayer(UUID player, boolean restore) {

        if (!this.isPlaying(player)) {
            return;
        }


        if (this.getTeamManagerModule().getPlayersOnTeam(this.getTeamManagerModule().getTeam(player)).size()-1 == 0 && !getPlayerInfo(player).getTeamInfo().isBedDestroyed()) {
            this.setBedBroken(getPlayerInfo(player).getTeam(), true);
        }

        this.getTeamManagerModule().removePlayer(player);

        this.getModule(ModuleGameItems.class).getGameItem(ItemInvisPot.class).removeInvisible(Bukkit.getPlayer(player));
        this.getModule(ModuleScoreboard.class).getScoreboard().getTeam(this.getPlayerInfo(player).getTeam().toString()).removeEntry(Bukkit.getPlayer(player).getName());


        if (restore) {
            SavedPlayerState state = getPlayerInfo(player).getSavedPlayerState();
            if (state != null) {
                state.restore(Bukkit.getPlayer(player));
                getPlayerInfo(player).setSavedPlayerState(null);
            }
        }


        if (running) {
            if (getTeamManagerModule().getTeams().size() < 2) {
                //Game Over
                if (getTeamManagerModule().getTeams().size() < 1) {
                    this.stop("No one won the game!", GameStopReason.GAME_END);
                } else {
                    BedwarsTeam winningTeam = BedwarsTeam.getTeam(getTeamManagerModule().getTeams().get(0));
                    assert winningTeam != null;
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        p.sendTitle(winningTeam.getChatColour() + winningTeam.getName() + ChatColor.GRAY + " has won the game!", "");
                        p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1f, 0.8f);
                    });

                    sendServerMessage(winningTeam.getChatColour() + winningTeam.getName() + ChatColor.GRAY + " has won the game!", "Game");

                    for(UUID ids : getTeamManagerModule().getPlayersOnTeam(winningTeam.getName())) {
                        Player player1 = Bukkit.getPlayer(ids);
                        if(player1 == null) continue;
                        Firework work = (Firework) player1.getWorld().spawnEntity(player1.getLocation(), EntityType.FIREWORK);
                        FireworkMeta meta = work.getFireworkMeta();
                        meta.addEffect(FireworkEffect.builder().flicker(true).trail(true).withColor(Color.fromRGB(winningTeam.getColour())).build());
                        meta.setPower(1);
                        work.setFireworkMeta(meta);
                        new InstantFirework(FireworkEffect.builder().flicker(true).withColor(Color.fromRGB(winningTeam.getColour())).build(), player1.getLocation());
                    }

                    ArrayList<BWPlayerInfo> playerInfos = Lists.newArrayList(playerInfo);
                    playerInfos.sort(Comparator.comparingInt(BWPlayerInfo::getKills).reversed());

                    sendServerMessage("&6&l&nTop Killers", "Stats");
                    sendServerMessage("", "Stats");
                    for (int i = 0; i < playerInfos.size() && i < 3; i++) {
                        BWPlayerInfo info = playerInfos.get(i);
                        sendServerMessage("&e" + (i + 1) + ". &f" + info.getName() + "&e -> &a" + info.getKills(), "Stats");
                    }
                    this.running = false;

                    //Will run in 5 seconds
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            stop("Good Job!", GameStopReason.GAME_END);
                        }
                    }.runTaskLater(CoreHandler.main, 100); //5 second delay
                }
                return;
            }
            sendGameMessage(Bukkit.getPlayer(player), "Making you a spectator", "Game");
            this.addSpectator(player);
        }
    }

    public void sendGameMessage(String message, String type) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        type = ChatColor.translateAlternateColorCodes('&', type);
        for (UUID ids : getPlayers()) {
            Bukkit.getPlayer(ids).sendMessage(ChatColor.BLUE + type + " > " + ChatColor.GRAY + message);
        }
    }

    public void sendServerMessage(String message, String type) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        type = ChatColor.translateAlternateColorCodes('&', type);
        for (Player players : Bukkit.getOnlinePlayers()) {
            players.sendMessage(ChatColor.BLUE + type + " > " + ChatColor.GRAY + message);
        }
    }

    public void sendGameMessage(CommandSender receiver, String message, String type) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        type = ChatColor.translateAlternateColorCodes('&', type);
        receiver.sendMessage(ChatColor.BLUE + type + " > " + ChatColor.GRAY + message);
    }

    private void internalEntityCleanup() {
        Vector v = mapRegion.getCenter();
        int x = mapRegion.getWidth();
        int z = mapRegion.getLength();
        int y = mapRegion.getHeight();
        World world = Bukkit.getWorld(Objects.requireNonNull(mapRegion.getWorld()).getName());
        for (Entity ents : world.getNearbyEntities(new Location(world, v.getBlockX(), v.getBlockY(), v.getBlockZ()), x / 2d, y / 2d, z / 2d)) {
            if (ents instanceof Item || ents instanceof Projectile) {
                ents.remove();
            }
        }
    }

    public void setBedBroken(@NotNull BedwarsTeam team, boolean value) {
        this.setBedBroken(team, value, "Supernatural Forces");
    }

    public void setBedBroken(@NotNull BedwarsTeam team, boolean value, String by) {
        this.getTeamInfo(team).setBedDestroyed(value);
        if (value && isRunning()) {
            //Bed break
            getPlayers().forEach(p -> {
                Player pl = Bukkit.getPlayer(p);
                //Sound when player breaks bed
                pl.playSound(pl.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 30f, 1f);
            });

            Location bedLocation = this.pointTracker.getBed(team);

            InstantFirework firework = new InstantFirework(FireworkEffect.builder().flicker(false).withColor(Color.WHITE).build(), bedLocation.clone().add(0.5, 0.2, 0.5));

            getTeamManagerModule().getPlayersOnTeam(team.toString()).forEach(m -> {
                Player p = Bukkit.getPlayer(m);
                if (p == null) return;
                p.sendTitle(ChatColor.RED + "Your bed has been broken!", "You will no longer respawn!");
            });

            if (Bukkit.getPlayer(by) != null) {
                BWPlayerInfo info = getPlayerInfo(Bukkit.getPlayer(by).getUniqueId());
                info.setBedsDestroyed(info.getBedsDestroyed() + 1);
            }

            sendGameMessage(team.getChatColour() + team.getName() + "&7's bed has been&c&l broken &7by &6" + by + "&7, their players can no longer respawn!", "Game");
        }
    }

    public boolean isRespawning(UUID player) {
        return this.getPlayerInfo(player) != null && this.getPlayerInfo(player).isRespawning();
    }

    public boolean addPlayer(UUID player, Location spawnpoint, BedwarsTeam team) {
        if (this.isSpectating(player)) {
            this.kickSpectator(player);
        }
        Player p = Bukkit.getPlayer(player);
        p.getEnderChest().clear();

        if (this.getPlayerInfo(player) == null) {
            this.playerInfo.add(new BWPlayerInfo(this, team, player, p.getName()));
        }

        this.getPlayerInfo(player).setSavedPlayerState(getModule(ModulePlayerSetup.class).spawnPlayer(p, spawnpoint, BedwarsPlayer.getPlayer(player).getKit(), team));

        return true;
    }

    @Override
    public boolean addPlayer(UUID player) {
        BedwarsTeam team = BedwarsTeam.values()[new Random(System.currentTimeMillis()).nextInt(BedwarsTeam.values().length)];
        return this.addPlayer(player,
                team);
    }

    public boolean addPlayer(UUID player, BedwarsTeam team) {
        return this.addPlayer(player,
                getPointTracker().getSpawnpoints(team).get(0),
                team);
    }

    @Override
    public boolean addSpectator(UUID player) {
        spectating.add(player);

        if(this.getPlayerInfo(player) == null){
            this.playerInfo.add(new BWPlayerInfo(this, BedwarsTeam.BLUE, player, Bukkit.getPlayer(player).getName()));
        }

        if (this.getPlayerInfo(player).getSavedPlayerState() == null) {
            this.getPlayerInfo(player).setSavedPlayerState(new SavedPlayerState(Bukkit.getPlayer(player)));
        }

        getModule(ModulePlayerSetup.class).makeSpectator(Bukkit.getPlayer(player));

        return true;
    }

    @Override
    public void kickSpectator(UUID player) {

        if(this.getPlayerInfo(player) == null){
            return;
        }

        SavedPlayerState state = this.getPlayerInfo(player).getSavedPlayerState();
        spectating.remove(player);

        if (state != null) {
            state.restore(Bukkit.getPlayer(player));
            HideUtil.unHidePlayer(Bukkit.getPlayer(player));
            this.getPlayerInfo(player).setSavedPlayerState(null);
        }
    }

    @Override
    public boolean isSpectating(UUID player) {
        return this.spectating.contains(player);
    }

    @Override
    public ArrayList<UUID> getPlayers() {
        return this.getTeamManagerModule().getPlayers();
    }

    @Override
    public ArrayList<UUID> getSpectators() {
        return this.spectating;
    }

    @Override
    public boolean isPlaying(UUID player) {
        return getTeamManagerModule().isContained(player);
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @EventSubscription
    public void onJoin(PlayerJoinEvent event) {
        if (this.isRunning()) {
            if (this.getPlayerInfo(event.getPlayer().getUniqueId()) != null) {
                BWPlayerInfo playerInfo = getPlayerInfo(event.getPlayer().getUniqueId());
                if (!playerInfo.getTeamInfo().isBedDestroyed()) {
                    BedwarsTeam team = playerInfo.getTeam();
                    this.addPlayer(event.getPlayer().getUniqueId(), playerInfo.getTeam());
                    this.getTeamManagerModule().setTeam(event.getPlayer().getUniqueId(), team.getName());
                }
            }
        }
    }
}
