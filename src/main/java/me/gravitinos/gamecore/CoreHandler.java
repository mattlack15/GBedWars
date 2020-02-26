package me.gravitinos.gamecore;

import me.gravitinos.gamecore.handler.GameHandler;
import me.gravitinos.gamecore.queue.GameQueue;
import me.gravitinos.gamecore.util.EventSubscription;
import me.gravitinos.gamecore.util.EventSubscriptions;
import me.gravitinos.gamecore.util.Menus.Menu;
import me.gravitinos.gamecore.util.Menus.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoreHandler {
    public static Plugin main;
    public static CoreHandler instance;

    private ExecutorService asyncExecutor = Executors.newCachedThreadPool();

    public static CompletableFuture<Void> doInMainThread(Menu.Func func) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        if (Bukkit.isPrimaryThread()) {
            func.execute();
            future.complete(null);
        } else {
            new BukkitRunnable() {
                public void run() {
                    func.execute();
                    future.complete(null);
                }
            }.runTask(CoreHandler.main);
        }
        return future;
    }

    public Executor getAsyncExecutor(){
        return this.asyncExecutor;
    }

    public CoreHandler(Plugin mainPlugin){
        instance = this;
        main = mainPlugin;
        new EventSubscriptions();
        new MenuManager();
        EventSubscriptions.instance.subscribe(this);
    }

    // When a player leaves the server
    @EventSubscription
    public void onLeave(PlayerQuitEvent event){
        GameQueue.getQueues().forEach((q) -> q.unQueuePlayer(event.getPlayer().getUniqueId()));
        GameHandler.getGameHandlers().forEach((g) -> g.kickPlayer(event.getPlayer().getUniqueId()));
    }
}
