package me.gravitinos.bedwars.gamecore;

import me.gravitinos.bedwars.gamecore.channel.ProxyComm;
import me.gravitinos.bedwars.gamecore.party.PartyComm;
import me.gravitinos.bedwars.gamecore.util.EventSubscriptions;
import me.gravitinos.bedwars.gamecore.util.Menus.Menu;
import me.gravitinos.bedwars.gamecore.util.Menus.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoreHandler {
    public static JavaPlugin main;
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

    public CoreHandler(JavaPlugin mainPlugin){
        instance = this;
        main = mainPlugin;
        new EventSubscriptions();
        new MenuManager();
        new ProxyComm();
        new PartyComm();
        EventSubscriptions.instance.subscribe(this);
    }
}
