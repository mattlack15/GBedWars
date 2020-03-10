package me.gravitinos.bedwars.game;

import com.boydti.fawe.FaweAPI;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.gravitinos.bedwars.game.info.BWPlayerInfo;
import me.gravitinos.bedwars.game.info.BWTeamInfo;
import me.gravitinos.bedwars.game.module.*;
import me.gravitinos.bedwars.game.module.gameitems.*;
import me.gravitinos.bedwars.game.module.shop.PlayerDependantShop;
import me.gravitinos.bedwars.game.module.shop.Shop;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.gameitem.GameItemHandler;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.bedwars.gamecore.handler.GameHandler;
import me.gravitinos.bedwars.gamecore.handler.GameStopReason;
import me.gravitinos.bedwars.gamecore.scoreboard.ModuleScoreboard;
import me.gravitinos.bedwars.gamecore.scoreboard.SBElement;
import me.gravitinos.bedwars.gamecore.scoreboard.SBScope;
import me.gravitinos.bedwars.gamecore.team.ModuleTeamManager;
import me.gravitinos.bedwars.gamecore.util.ActionBar;
import me.gravitinos.bedwars.gamecore.util.Saving.SavedPlayerState;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
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

    public BedwarsHandler(File map) {
        super("Bedwars", 3600);
        new BedwarsMapDataHandler(map);
        this.pointTracker = new BedwarsMapPointTracker(BedwarsMapDataHandler.instance);


        //--- Team Info Setup ---
        for(BedwarsTeam teams : BedwarsTeam.values()){
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


        this.addModule(new ModuleTeamManager(this));
        this.addModule(new ModuleScoreboard(this, ChatColor.RED + "Soraxus BedWars", SBScope.EVERYONE));
        this.addModule(new ModuleDamageHandler(this));

        //--- Scoreboard Setup ---
        this.getModule(ModuleScoreboard.class).addElement(new SBElement(""));

        this.getModule(ModuleScoreboard.class).addElement(new SBElement("")
                .setTextGetter(() -> "&b&lBlue " + (!getTeamInfo(BedwarsTeam.BLUE).isBedDestroyed() ? "&a✔" : "&c✘")));
        this.getModule(ModuleScoreboard.class).addElement(new SBElement("")
                .setTextGetter(() -> "&c&lRed " + (!getTeamInfo(BedwarsTeam.RED).isBedDestroyed() ? "&a✔" : "&c✘")));
        this.getModule(ModuleScoreboard.class).addElement(new SBElement("")
                .setTextGetter(() -> "&a&lGreen " + (!getTeamInfo(BedwarsTeam.GREEN).isBedDestroyed() ? "&a✔" : "&c✘")));
        this.getModule(ModuleScoreboard.class).addElement(new SBElement("")
                .setTextGetter(() -> "&e&lYellow " + (!getTeamInfo(BedwarsTeam.YELLOW).isBedDestroyed() ? "&a✔" : "&c✘")));

        this.addModule(new ModuleGenerators(this, pointTracker.getMidGens(), pointTracker.getOuterGens(), pointTracker.getBaseGens()));
        this.addModule(new ModulePlayerSetup(this));

        //--- Shop Setup ---
        ArrayList<Shop> shops = new ArrayList<>();
        shops.add(new PlayerDependantShop(this, "&e&lShop", pointTracker.getShopBLUE(), this.getGameItemsModule()));
        shops.add(new PlayerDependantShop(this, "&e&lShop", pointTracker.getShopRED(), this.getGameItemsModule()));
        shops.add(new PlayerDependantShop(this, "&e&lShop", pointTracker.getShopYELLOW(), this.getGameItemsModule()));
        shops.add(new PlayerDependantShop(this, "&e&lShop", pointTracker.getShopGREEN(), this.getGameItemsModule()));

        this.addModule(new ModuleShops(this, shops));

        //Get map region
        Location b1 = this.pointTracker.getBorder1();
        Location b2 = this.pointTracker.getBorder2();

        this.mapRegion = new CuboidRegion(FaweAPI.getWorld(b1.getWorld().getName()), new com.sk89q.worldedit.Vector(b1.getX(), b1.getY(), b1.getZ()), new com.sk89q.worldedit.Vector(b2.getX(), b2.getY(), b2.getZ()));

        this.addModule(new ModuleGameEnvironment(this, mapRegion));
        this.addModule(new ModuleBorder(this, mapRegion));
    }

    public BWTeamInfo getTeamInfo(BedwarsTeam team){
        for(BWTeamInfo infos : teamInfo){
            if(infos.getTeam().equals(team)){
                return infos;
            }
        }
        return null;
    }

    public BWPlayerInfo getPlayerInfo(UUID player){
        for(BWPlayerInfo infos : playerInfo){
            if(infos.getUuid().equals(player)){
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
    public CompletableFuture<Boolean> start(ArrayList<UUID> players) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        CoreHandler.doInMainThread(() -> {

            this.running = true;

            //Startup
            try {
                this.getModule(ModuleGenerators.class).enable();
                this.getModule(ModuleScoreboard.class).setEnabled(true);
                this.getGameItemsModule().enableAllGameItems();

                this.getTeamManagerModule().clear();
                this.getTeamManagerModule().insert(players, BedwarsTeam.BLUE.toString(), BedwarsTeam.GREEN.toString(), BedwarsTeam.RED.toString(), BedwarsTeam.YELLOW.toString());

                ModulePlayerSetup playerSetup = this.getModule(ModulePlayerSetup.class);

                ArrayList<Location> spawnpointsBLUE = this.pointTracker.getSpawnpointsBLUE();
                ArrayList<Location> spawnpointsRED = this.pointTracker.getSpawnpointsRED();
                ArrayList<Location> spawnpointsGREEN = this.pointTracker.getSpawnpointsGREEN();
                ArrayList<Location> spawnpointsYELLOW = this.pointTracker.getSpawnpointsYELLOW();

                if (spawnpointsBLUE.size() == 0) {
                    this.stop("FATAL ERROR: Blue has no spawnpoints marked!", GameStopReason.GAME_ERROR);
                    future.complete(false);
                    return;
                }
                if (spawnpointsRED.size() == 0) {
                    this.stop("FATAL ERROR: Red has no spawnpoints marked!", GameStopReason.GAME_ERROR);
                    future.complete(false);
                    return;
                }
                if (spawnpointsGREEN.size() == 0) {
                    this.stop("FATAL ERROR: Green has no spawnpoints marked!", GameStopReason.GAME_ERROR);
                    future.complete(false);
                    return;
                }
                if (spawnpointsYELLOW.size() == 0) {
                    this.stop("FATAL ERROR: Yellow has no spawnpoints marked!", GameStopReason.GAME_ERROR);
                    future.complete(false);
                    return;
                }

                Collections.shuffle(spawnpointsBLUE);
                Collections.shuffle(spawnpointsYELLOW);
                Collections.shuffle(spawnpointsGREEN);
                Collections.shuffle(spawnpointsRED);

                int ib = 0;
                int ir = 0;
                int ig = 0;
                int iy = 0;
                for (UUID uuid : this.getTeamManagerModule().getPlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    this.playerInfo.add(new BWPlayerInfo(this, uuid));

                    if (this.getTeamManagerModule().getTeam(uuid).equals(BedwarsTeam.BLUE.toString())) {
                        if (ib >= spawnpointsBLUE.size()) {
                            ib = 0;
                        }
                        this.getPlayerInfo(uuid).setSavedPlayerState(playerSetup.spawnPlayer(player, spawnpointsBLUE.get(ib), BedwarsPlayer.getPlayer(player.getUniqueId()).getKit(), BedwarsTeam.BLUE));
                        ib++;
                    }

                    if (this.getTeamManagerModule().getTeam(uuid).equals(BedwarsTeam.RED.toString())) {
                        if (ir >= spawnpointsRED.size()) {
                            ir = 0;
                        }
                        this.getPlayerInfo(uuid).setSavedPlayerState(playerSetup.spawnPlayer(player, spawnpointsRED.get(ir), BedwarsPlayer.getPlayer(player.getUniqueId()).getKit(), BedwarsTeam.RED));
                        ir++;
                    }

                    if (this.getTeamManagerModule().getTeam(uuid).equals(BedwarsTeam.YELLOW.toString())) {
                        if (iy >= spawnpointsYELLOW.size()) {
                            iy = 0;
                        }
                        this.getPlayerInfo(uuid).setSavedPlayerState(playerSetup.spawnPlayer(player, spawnpointsYELLOW.get(iy), BedwarsPlayer.getPlayer(player.getUniqueId()).getKit(), BedwarsTeam.YELLOW));
                        iy++;
                    }

                    if (this.getTeamManagerModule().getTeam(uuid).equals(BedwarsTeam.GREEN.toString())) {
                        if (ig >= spawnpointsGREEN.size()) {
                            ig = 0;
                        }
                        this.getPlayerInfo(uuid).setSavedPlayerState(playerSetup.spawnPlayer(player, spawnpointsGREEN.get(ig), BedwarsPlayer.getPlayer(player.getUniqueId()).getKit(), BedwarsTeam.GREEN));
                        ig++;
                    }
                }
            } catch(Exception e){
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

        Bukkit.broadcastMessage(SpigotBedwars.PLUGIN_PREFIX + "Game ended, " + ChatColor.translateAlternateColorCodes('&', stopMessage));

        this.getModule(ModuleGenerators.class).cleanup();
        this.getModule(ModuleGenerators.class).disable();
        this.getGameItemsModule().disableAllGameItems();
        this.getModule(ModuleScoreboard.class).setEnabled(false);
        this.getModule(ModuleShops.class).cleanup();
        this.getModule(ModuleGameEnvironment.class).revertBlocks();

        this.internalEntityCleanup();

        for (UUID players : Lists.newArrayList(this.getTeamManagerModule().getPlayers())) {
            this.kickPlayer(players);
        }

        for (UUID spectators : Lists.newArrayList(this.spectating)) {
            this.kickSpectator(spectators);
        }

        return null;
    }

    public void killPlayer(@NotNull UUID player, @NotNull String cause) {
        Player p = Bukkit.getPlayer(player);

        cause = ChatColor.translateAlternateColorCodes('&', cause);

        BedwarsTeam team = BedwarsTeam.getTeam(getTeamManagerModule().getTeam(player));

        this.getModule(ModulePlayerSetup.class).killPlayer(p, this.getTeamInfo(team).isBedDestroyed());

        if (this.getTeamInfo(team).isBedDestroyed()) {
            this.kickPlayer(player);
            this.sendGameMessage(ChatColor.RED + p.getName() + ChatColor.GRAY + " has been " + ChatColor.AQUA + "eliminated" + ChatColor.GRAY + " by " + ChatColor.YELLOW + cause, "Death");
            return;
        }

        if (this.getPlayerInfo(player).isRespawning()) {
            return;
        }

        this.sendGameMessage(ChatColor.RED + p.getName() + ChatColor.GRAY + " has been killed by " + ChatColor.YELLOW + cause, "Death");

        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT, 10f, 1f);
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
        this.getTeamManagerModule().removePlayer(player);

        if (restore) {
            SavedPlayerState state = getPlayerInfo(player).getSavedPlayerState();
            if (state != null) {
                state.restore(Bukkit.getPlayer(player));
                getPlayerInfo(player).setSavedPlayerState(null);
            }
        }

        if (running) {
            if (getTeamManagerModule().getTeams().size() < 2) {
                if (getTeamManagerModule().getTeams().size() < 1) {
                    this.stop("No one won the game!", GameStopReason.GAME_END);
                } else {
                    this.stop("Winner is " + getTeamManagerModule().getTeams().get(0) + "!", GameStopReason.GAME_END);
                }
            }
        }
    }

    public void sendGameMessage(String message, String type) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        type = ChatColor.translateAlternateColorCodes('&', type);
        for (UUID ids : getPlayers()) {
            Bukkit.getPlayer(ids).sendMessage(ChatColor.BLUE + type + " > " + ChatColor.GRAY + message);
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
        this.getTeamInfo(team).setBedDestroyed(value);
    }

    public boolean isRespawning(UUID player) {
        return this.getPlayerInfo(player).isRespawning();
    }

    @Override
    public boolean addPlayer(UUID player) {
        return false;
    }

    @Override
    public boolean addSpectator(UUID player) {
        spectating.add(player);

        if (this.getPlayerInfo(player).getSavedPlayerState() == null) {
            this.getPlayerInfo(player).setSavedPlayerState(new SavedPlayerState(Bukkit.getPlayer(player)));
        }

        getModule(ModulePlayerSetup.class).makeSpectator(Bukkit.getPlayer(player));

        return true;
    }

    @Override
    public void kickSpectator(UUID player) {

        SavedPlayerState state = this.getPlayerInfo(player).getSavedPlayerState();
        spectating.remove(player);

        if (state != null) {
            state.restore(Bukkit.getPlayer(player));
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
}
