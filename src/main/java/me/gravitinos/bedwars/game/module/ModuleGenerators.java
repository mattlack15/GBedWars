package me.gravitinos.bedwars.game.module;

import me.gravitinos.bedwars.game.module.generator.Generator;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.handler.GameHandler;
import me.gravitinos.bedwars.gamecore.module.GameModule;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class ModuleGenerators extends GameModule {

    private static final double MID_GENERATOR_INTERVAL = 20;
    private static final double OUTER_GENERATOR_INTERVAL = 40;
    private static final double BASE_GENERATOR_IRON_INTERVAL = 1.2;
    private static final double BASE_GENERATOR_GOLD_INTERVAL = 8;

    private boolean enabled = false;

    private ArrayList<Generator> generators = new ArrayList<>();

    public ModuleGenerators(@NotNull GameHandler gameHandler, ArrayList<Location> midGens, ArrayList<Location> outerGens, ArrayList<Location> baseGens) {
        super(gameHandler, "GENERATORS");

        //Mid and outer generators
        for (Location outerGen : outerGens) {
            generators.add(new Generator(outerGen, ChatColor.AQUA + "Diamond Generator", new ItemBuilder(Material.DIAMOND, 1).addLore(ChatColor.GRAY + "Use this in the shop for advantages").build(), MID_GENERATOR_INTERVAL));
        }
        for (Location midGen : midGens) {
            generators.add(new Generator(midGen, ChatColor.GREEN + "Emerald Generator", new ItemBuilder(Material.EMERALD, 1).addLore(ChatColor.GRAY + "Use this in the shop for gear and items").build(), OUTER_GENERATOR_INTERVAL));
        }

        //Stand setup
        generators.forEach(gen -> {
            gen.createStand(gen.getName() + " " + ChatColor.YELLOW + Math.round(gen.countdown) + "s");
            gen.createItem();
        });

        //Base generators
        for (Location baseGen : baseGens) {
            generators.add(new Generator(baseGen, ChatColor.GREEN + "Base Generator Iron", new ItemBuilder(Material.IRON_INGOT, 1).build(), BASE_GENERATOR_IRON_INTERVAL));
            generators.add(new Generator(baseGen, ChatColor.GREEN + "Base Generator Gold", new ItemBuilder(Material.GOLD_INGOT, 1).build(), BASE_GENERATOR_GOLD_INTERVAL));
        }

        //Task interval of 2 ticks or 0.1s
        new BukkitRunnable(){
            @Override
            public void run() {
                if(!enabled){
                    return;
                }
                for (Generator generator : generators) {

                    //Decrement countdown
                    generator.countdown-=0.1d;

                    //Check if it is time to drop an item
                    if(generator.countdown <= 0){
                        generator.countdown = generator.getInterval(); //Reset countdown
                        generator.dropItem(1); //Drop one
                    }

                    //Set text
                    generator.setText(generator.getName() + " " + ChatColor.YELLOW + Math.round(generator.countdown) + "s");
                }
            }
        }.runTaskTimer(CoreHandler.main, 0, 2);
    }

    public ArrayList<Generator> getGenerators(){
        return this.generators;
    }

    public void enable(){
        this.enabled = true;
    }

    public void disable(){
        this.enabled = false;
    }

    public void cleanup(){
        generators.forEach(gen -> {
            gen.removeStand();
            gen.removeItem();
        });
    }

    public void clearGenerators(){
        generators.forEach(gen -> {
            gen.removeStand();
            gen.removeItem();
        });
        generators.clear();
    }

    /**
     * Adds a generator
     * @param generator The generator to add
     * @param createStand Whether or not to create an armor-stand for holographic text
     * @param createItem Whether or not to create a display-item
     */
    public void addGenerator(Generator generator, boolean createStand, boolean createItem){
        this.generators.add(generator);
        if(createItem){
            generator.createItem();
        }
        if(createStand){
            generator.createStand(generator.getName() + " " + ChatColor.YELLOW + Math.round(generator.countdown) + "s");
        }
    }

    //Item pickup cancelling
    @EventSubscription
    private void onPickup(EntityPickupItemEvent event){
        for(Generator gens : generators){
            if(gens.getItem() != null){
                if(gens.getItem().getUniqueId().equals(event.getItem().getUniqueId())){
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventSubscription
    private void onDespawn(ItemDespawnEvent event){
        for(Generator gens : generators){
            if(gens.getItem() != null){
                if(gens.getItem().getUniqueId().equals(event.getEntity().getUniqueId())){
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

}
