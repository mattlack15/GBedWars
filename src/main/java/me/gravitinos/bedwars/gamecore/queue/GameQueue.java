package me.gravitinos.bedwars.gamecore.queue;

import com.google.common.collect.Lists;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.handler.GameHandler;
import me.gravitinos.bedwars.gamecore.util.ActionBar;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import me.gravitinos.bedwars.gamecore.util.EventSubscriptions;
import me.gravitinos.bedwars.gamecore.util.WeakList;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

    private static WeakList<GameQueue> queues = new WeakList<>();

    private String actionBarMessage = "";

    private static boolean isTaskRunning = false;

    private static BukkitRunnable task = new BukkitRunnable() {
        @Override
        public void run() {
                for(GameQueue queues : queues) {
                    if (queues.isRunning()) {
                        queues.timeLeftSeconds -= TASK_INCREMENT;
                        if(queues.timeLeftSeconds <= 0){
                            queues.end();
                        } else if(queues.showActionBar){
                            ActionBar.sendAll(queues.actionBarMessage.replace("<timeLeftSeconds>", (Math.round(queues.timeLeftSeconds*10d)/10d) + "")
                            .replace("<numQueued>", queues.queued.size() + "")
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
                }
            } catch(Exception e){
                Bukkit.broadcastMessage("Problem in GameQueue! Contact Dev");
            }
        }
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
     * Resets time to original value
     */
    public void resetTime(){
        this.timeLeftSeconds = originalTimeSeconds;
    }

    /**
     * Resets time
     * @param timeLeftSeconds The time to set it back to
     */
    public void resetTime(double timeLeftSeconds){
        this.timeLeftSeconds = timeLeftSeconds;
    }

    /**
     * Queues a player in this queue
     * @param player The player to queue
     * @return Whether the player was queued or not, may return false if the player is already queued or the player is not online
     */
    public boolean queuePlayer(UUID player){
        if(this.queued.contains(player) || Bukkit.getPlayer(player) == null){
            return false;
        }
        this.queued.add(player);
        return true;
    }

    /**
     * Un-queues a player from this queue
     * @param player The player to un-queue
     */
    public void unQueuePlayer(UUID player){
        this.queued.remove(player);
    }

    /**
     * Checks if a player is currently queued in this queue
     * @param player The player to check for
     * @return Whether the player is queued or not
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
        this.timeLeftSeconds = this.originalTimeSeconds;
        ArrayList<UUID> q = Lists.newArrayList(queued);
        CoreHandler.doInMainThread(() -> game.start(q));
        this.queued.clear();
    }

    @EventSubscription
    private void onLeave(PlayerQuitEvent event){
        this.unQueuePlayer(event.getPlayer().getUniqueId());
    }

}
