package me.gravitinos.bedwars.game.module;

import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.bedwars.game.module.generator.Generator;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.gameitem.GameItemHandler;
import me.gravitinos.bedwars.gamecore.handler.GameHandler;
import me.gravitinos.bedwars.gamecore.module.GameModule;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class ModuleGenerators extends GameModule {

    private static final double MID_GENERATOR_INTERVAL = 40;
    private static final double OUTER_GENERATOR_INTERVAL = 20;
    private static final double BASE_GENERATOR_IRON_INTERVAL = 1.2;
    private static final double BASE_GENERATOR_GOLD_INTERVAL = 8;

    private boolean enabled = false;

    private ArrayList<Generator> generators = new ArrayList<>();

    public ModuleGenerators(@NotNull GameHandler gameHandler, ArrayList<Location> midGens, ArrayList<Location> outerGens, ArrayList<Location> baseGens) {
        super(gameHandler, "GENERATORS");

        //Mid and outer generators
        for (Location outerGen : outerGens) {
            generators.add(new Generator(outerGen, ChatColor.AQUA + "Diamond Generator", Objects.requireNonNull(a(BedwarsItem.RESOURCE_DIAMOND.toString())), OUTER_GENERATOR_INTERVAL).setDisplayItem(new ItemStack(Material.DIAMOND_BLOCK)));
        }
        for (Location midGen : midGens) {
            generators.add(new Generator(midGen, ChatColor.GREEN + "Emerald Generator", Objects.requireNonNull(a(BedwarsItem.RESOURCE_EMERALD.toString())), MID_GENERATOR_INTERVAL).setDisplayItem(new ItemStack(Material.EMERALD_BLOCK)));
        }

        //Stand setup
        generators.forEach(gen -> {
            gen.createStand(gen.getName() + " " + ChatColor.YELLOW + Math.round(gen.countdown) + "s");
            gen.createItem();
        });

        //Base generators
        for (Location baseGen : baseGens) {
            generators.add(new Generator(baseGen, ChatColor.GREEN + "Base Generator Iron", Objects.requireNonNull(a(BedwarsItem.RESOURCE_IRON.toString())), BASE_GENERATOR_IRON_INTERVAL).setMultipleItemGiving(true));
            generators.add(new Generator(baseGen, ChatColor.GREEN + "Base Generator Gold", Objects.requireNonNull(a(BedwarsItem.RESOURCE_GOLD.toString())), BASE_GENERATOR_GOLD_INTERVAL).setMultipleItemGiving(true));
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
                    generator.countdown-=0.05d;

                    //Check if it is time to drop an item
                    if(generator.countdown <= 0){
                        generator.countdown = generator.getInterval(); //Reset countdown
                        generator.dropItem(1); //Drop one
                    }

                    ArmorStand item = generator.getItem();
                    if(item != null){
                        EulerAngle headPose = item.getHeadPose();
                        if(headPose.getY() <= 0){
                            headPose = new EulerAngle(0, Math.toRadians(360), 0);
                        }
                        headPose = new EulerAngle(0, headPose.getY()-Math.toRadians(5), 0);
                        item.setHeadPose(headPose);
                    }

                    //Set text
                    generator.setText(generator.getName() + " " + ChatColor.YELLOW + Math.round(generator.countdown) + "s");
                }
            }
        }.runTaskTimer(CoreHandler.main, 0, 1);
    }

    private ItemStack a(String item){
        GameItemHandler gameItemHandler = ((BedwarsHandler)this.getGameHandler()).getGameItemsModule().getGameItem(item);
        return gameItemHandler != null ? gameItemHandler.getItem(1) : null;
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
