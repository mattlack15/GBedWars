package me.gravitinos.minigame.gamecore.queue;

import com.google.common.collect.Lists;
import me.gravitinos.minigame.gamecore.CoreHandler;
import me.gravitinos.minigame.gamecore.module.GameHandler;
import me.gravitinos.minigame.gamecore.party.BaseParty;
import me.gravitinos.minigame.gamecore.party.BungeeParty;
import me.gravitinos.minigame.gamecore.party.BungeePartyFactory;
import me.gravitinos.minigame.gamecore.util.ActionBar;
import me.gravitinos.minigame.gamecore.util.EventSubscription;
import me.gravitinos.minigame.gamecore.util.EventSubscriptions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class GameQueue {
    private static final double TASK_INCREMENT = 0.1d;

    private double timeLeftSeconds;
    private boolean running = false;
    private double originalTimeSeconds;
    private ArrayList<UUID> queued = new ArrayList<>();
    private GameHandler game;
    private int maxNumQueued = 30;
    private boolean showActionBar = true;

    private static ArrayList<GameQueue> queues = new ArrayList<>();

    private ArrayList<Runnable> actions = new ArrayList<>();

    private boolean preStarted = false;

    private String actionBarMessage = "";

    private static boolean isTaskRunning = false;

    private static BukkitRunnable task = new BukkitRunnable() {
        @Override
        public void run() {
                for(GameQueue queues : queues) {
                    if (queues.isRunning()) {
                        queues.timeLeftSeconds -= TASK_INCREMENT;
                        if(queues.timeLeftSeconds < 10 && !queues.preStarted) {
                            queues.preStarted = true;
                            if (!queues.game.onPreStart()) {
                                queues.resetTime();
                                queues.pause();
                                Bukkit.broadcastMessage(ChatColor.RED + "ERROR: Pre-Startup reported error! Canceling queue.");
                            }
                        }
                        if(queues.timeLeftSeconds <= 0){
                            queues.end();
                        } else if(queues.showActionBar){
                            ActionBar.sendAll(queues.actionBarMessage.replace("<timeLeftSeconds>", (Math.round(queues.timeLeftSeconds*10d)/10d) + "")
                            .replace("<numQueued>", queues.getNumPlayersQueued() + "")
                            .replace("<maxQueued>", queues.maxNumQueued + ""));
                        }
                    }
                }
        }
    };

    /**
     * Sets the actionbar message
     * @param actionBarMessage message, can use timeLeftSeconds, numQueued and maxQueued (Max amount of players allowed in queue) surrounded by a < and a > in the message
     */
    public void setActionBarMessage(String actionBarMessage) {
        this.actionBarMessage = actionBarMessage;
    }

    public void setMaxNumQueued(int maxNumQueued) {
        this.maxNumQueued = maxNumQueued;
    }

    public int getMaxNumQueued() {
        return maxNumQueued;
    }

    /**
     * Gets all currently existing queues, running or not
     * @return List of the existing queues
     */
    public static ArrayList<GameQueue> getQueues(){
        synchronized (GameQueue.class){
            return Lists.newArrayList(queues);
        }
    }

    public boolean isShowActionBar() {
        return showActionBar;
    }

    public void setShowActionBar(boolean showActionBar) {
        this.showActionBar = showActionBar;
    }

    public ArrayList<UUID> getQueued() {
        return Lists.newArrayList(queued);
    }

    /**
     * Gets the time left in seconds
     * @return Time left in seconds
     */
    public double getTimeLeftSeconds() {
        return timeLeftSeconds;
    }

    /**
     * Register queue with the static queue manager
     * @param queue The queue to register
     */
    private static void register(GameQueue queue){

        synchronized (GameQueue.class) {
            if (!queues.contains(queue)) {
                queues.add(queue);
            }
            try {
                if (!isTaskRunning) {
                    task.runTaskTimer(CoreHandler.main, 0, Math.round(20 * TASK_INCREMENT));
                    isTaskRunning = true;
                }
            } catch(Exception e){
                Bukkit.broadcastMessage("Problem in GameQueue! Contact Dev");
            }
        }
    }

    /**
     * Add something to be run when the queue ends, before the game is started
     * @param func The function
     */
    public void addAction(Runnable func){
        this.actions.add(func);
    }

    public void clearQueued(){
        this.queued.clear();
    }

    /**
     * Constructor
     * @param game The game to start when the queue ends
     * @param timeSeconds The initial time to start with (in seconds)
     */
    public GameQueue(@NotNull GameHandler game, int timeSeconds){
        this.timeLeftSeconds = timeSeconds;
        this.originalTimeSeconds = timeSeconds;
        this.game = game;
        register(this);
        EventSubscriptions.instance.subscribe(this);
    }

    /**
     * Gets all the queued players
     * @return List of queued players
     */
    public ArrayList<UUID> getPlayersQueued(){
        return Lists.newArrayList(this.queued);
    }

    /**
     * Get the amount of players queued
     * @return The amount of players queued
     */
    public int getNumPlayersQueued(){
        return this.queued.size();
    }

    /**
     * Resets time to original value
     */
    public void resetTime(){
        this.resetTime(originalTimeSeconds);
    }

    /**
     * Resets time
     * @param timeLeftSeconds The time to set it back to
     */
    public void resetTime(double timeLeftSeconds){
        this.timeLeftSeconds = timeLeftSeconds;
        this.preStarted = false;
    }

    /**
     * Queues a player
     * @param player the player
     * @return Whether the player was queued or not, may return false if the player is already queued
     */
    public boolean queuePlayer(UUID player){
        if(this.queued.contains(player)){
            return false;
        }
        this.queued.add(player);
        return true;
    }

    /**
     * Un-queues a player from this queue
     * @param player the player
     */
    public void unQueuePlayer(@NotNull UUID player){
        this.queued.remove(player);
    }

    /**
     * Checks if a player is currently queued in this queue
     * @param player the player to check for
     * @return Whether the party is queued or not
     */
    public boolean isPlayerQueued(UUID player){
        return this.queued.contains(player);
    }

    /**
     * Checks if this queue is currently running
     * @return Whether or not this queue is currently running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Starts this queue
     */
    public void start(){
        this.running = true;
    }

    /**
     * Pauses this queue
     */
    public void pause(){
        this.running = false;
    }

    /**
     * Ends the queue and starts the game
     */
    public void end(){
        this.running = false;
        if(!preStarted){
            if(!game.onPreStart()){
                this.resetTime();
                this.pause();
                Bukkit.broadcastMessage(ChatColor.RED + "ERROR: Pre-Startup reported error! Canceling queue.");
                return;
            }
            preStarted = true;
        }
        this.resetTime();

        ArrayList<BaseParty> parties = new ArrayList<>();
        queued.forEach(q -> parties.add(BungeePartyFactory.getPartyOf(q) != null ? BungeePartyFactory.getPartyOf(q) : BungeePartyFactory.createNewParty(q)));

        this.actions.forEach(Runnable::run);
        CoreHandler.doInMainThread(() -> game.start(parties));
        this.queued.clear();
    }

    @EventSubscription
    private void onLeave(PlayerQuitEvent event){
        this.unQueuePlayer(event.getPlayer().getUniqueId());
    }

}
