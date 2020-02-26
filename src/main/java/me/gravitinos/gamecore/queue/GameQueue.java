package me.gravitinos.gamecore.queue;

import com.google.common.collect.Lists;
import me.gravitinos.gamecore.CoreHandler;
import me.gravitinos.gamecore.handler.GameHandler;
import me.gravitinos.gamecore.util.WeakList;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class GameQueue {
    private static final double TASK_INCREMENT = 0.1d;

    private double timeLeftSeconds;
    private boolean running = false;
    private ArrayList<UUID> queued = new ArrayList<>();
    private GameHandler game;

    private static WeakList<GameQueue> queues = new WeakList<>();

    private static BukkitRunnable task = new BukkitRunnable() {
        @Override
        public void run() {
            synchronized (GameQueue.class) {
                for(GameQueue queues : queues) {
                    if (queues.isRunning()) {
                        queues.timeLeftSeconds -= TASK_INCREMENT;
                        if(queues.timeLeftSeconds <= 0){
                            queues.end();
                        }
                    }
                }
            }
        }
    };

    /**
     * Gets all currently existing queues, running or not
     * @return List of the existing queues
     */
    public static ArrayList<GameQueue> getQueues(){
        synchronized (GameQueue.class){
            return Lists.newArrayList(queues);
        }
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

            if (!Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId())) {
                task.runTaskTimer(CoreHandler.main, 0, Math.round(20 * TASK_INCREMENT));
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
        this.game = game;
        register(this);
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
        CoreHandler.doInMainThread(() -> game.start(this.queued));
    }

}
