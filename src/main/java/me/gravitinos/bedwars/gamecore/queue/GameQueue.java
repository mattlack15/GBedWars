package me.gravitinos.bedwars.gamecore.queue;

import com.google.common.collect.Lists;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.module.GameHandler;
import me.gravitinos.bedwars.gamecore.party.BaseParty;
import me.gravitinos.bedwars.gamecore.util.ActionBar;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import me.gravitinos.bedwars.gamecore.util.EventSubscriptions;
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
    private ArrayList<BaseParty> queued = new ArrayList<>();
    private GameHandler game;
    private int maxNumQueued = 30;
    private boolean showActionBar = true;

    private static ArrayList<GameQueue> queues = new ArrayList<>();

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

    public ArrayList<BaseParty> getQueued() {
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
        ArrayList<UUID> players = new ArrayList<>();
        this.queued.forEach(p -> players.addAll(p.getMembers()));
        return players;
    }

    /**
     * Get the amount of players queued
     * @return The amount of players queued
     */
    public int getNumPlayersQueued(){
        int num = 0;
        for (BaseParty party : this.queued) {
            num += party.getMembers().size();
        }
        return num;
    }

    /**
     * Checks if a player is queued
     * @param playerUUID The UUID of the player to check for
     * @return The result
     */
    public boolean isPlayerQueued(UUID playerUUID){
        for(BaseParty parties : Lists.newArrayList(this.queued)){
            if(parties.getMembers().contains(playerUUID)){
                return true;
            }
        }
        return false;
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
     * Queues a party in this queue
     * @param party The party to queue
     * @return Whether the party was queued or not, may return false if the party is already queued
     */
    public boolean queueParty(BaseParty party){
        if(this.queued.contains(party)){
            return false;
        }
        this.queued.add(party);
        return true;
    }

    /**
     * Un-queues a party from this queue
     * @param partyId The id of the party to un-queue
     */
    public void unQueueParty(@NotNull UUID partyId){
        for(BaseParty parties : Lists.newArrayList(this.queued)){
            if(parties.getPartyId().equals(partyId)){
                this.queued.remove(parties);
            }
        }
    }

    /**
     * Checks if a party is currently queued in this queue
     * @param partyId The id of the party to check for
     * @return Whether the party is queued or not
     */
    public boolean isPartyQueued(UUID partyId){
        for(BaseParty parties : Lists.newArrayList(this.queued)){
            if(parties.getPartyId().equals(partyId)){
                return true;
            }
        }
        return false;
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
        ArrayList<BaseParty> q = Lists.newArrayList(queued);
        CoreHandler.doInMainThread(() -> game.start(q));
        this.queued.clear();
    }

    @EventSubscription
    private void onLeave(PlayerQuitEvent event){
        for (BaseParty party : Lists.newArrayList(this.queued)) {
            if(!party.getMembers().contains(event.getPlayer().getUniqueId())){
                continue;
            }
            if(party.isOnePersonParty()){
                this.unQueueParty(party.getPartyId());
            }
        }
    }

}
