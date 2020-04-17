package me.gravitinos.minigame.bedwars.game.module;

import com.google.common.collect.Lists;
import me.gravitinos.minigame.gamecore.CoreHandler;
import me.gravitinos.minigame.gamecore.module.GameHandler;
import me.gravitinos.minigame.gamecore.module.GameModule;
import me.gravitinos.minigame.gamecore.util.EntityStore;
import me.gravitinos.minigame.gamecore.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class ModuleCoolDeaths extends GameModule {

    private static final int ITEM_LIFE_MS = 1000;

    private Map<EntityStore<Item>, Long> items = new HashMap<>();

    public ModuleCoolDeaths() {
        super("COOL_DEATHS");
    }

    private BukkitRunnable getTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                long ctm = System.currentTimeMillis();
                for (EntityStore<Item> item : Lists.newArrayList(items.keySet())) {
                    if (item.getEntity() == null) {
                        items.remove(item);
                        continue;
                    }

                    if (ctm - items.get(item) >= ITEM_LIFE_MS) {
                        item.getEntity().remove();
                        items.remove(item);
                    }
                }
            }
        };
    }

    int currentTaskId = -1;

    @Override
    public void enable() {
        super.enable();
        currentTaskId = getTask().runTaskTimer(CoreHandler.main, 0, 2).getTaskId();
    }


    public void bloodDeath(Player p){
        ItemStack stack = new ItemBuilder(Material.INK_SACK, 1, (byte) 1).build();

        Random rand = new Random(System.currentTimeMillis());
        for(int i = 0; i < 8; i++){
            double x = rand.nextDouble() * 0.2 - 0.1;
            double y = rand.nextDouble() * 0.2 - 0.1;
            double z = rand.nextDouble() * 0.2 - 0.1;

            Vector randomVel = new Vector(x, y, z);
            randomVel.add(p.getVelocity().multiply(1.2));
            Item item = p.getWorld().dropItem(p.getLocation().add(0, 0.5, 0), stack);
            item.setVelocity(randomVel);
            item.setPickupDelay(10000);
            this.items.put(new EntityStore<>(item), System.currentTimeMillis());
        }
    }

    public void disable(){
        super.disable();
        if(currentTaskId != -1)
            Bukkit.getScheduler().cancelTask(currentTaskId);
        for(EntityStore<Item> item : Lists.newArrayList(items.keySet())){
            if(item.getEntity() == null)
                items.remove(item);

            item.getEntity().remove();
            items.remove(item);
        }
    }
}
