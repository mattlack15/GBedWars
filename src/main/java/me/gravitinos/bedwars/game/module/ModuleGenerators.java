package me.gravitinos.bedwars.game.module;

import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.bedwars.game.module.generator.Generator;
import me.gravitinos.bedwars.game.module.generator.GeneratorDrop;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.gameitem.GameItemHandler;
import me.gravitinos.bedwars.gamecore.handler.GameHandler;
import me.gravitinos.bedwars.gamecore.module.GameModule;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import me.gravitinos.bedwars.gamecore.util.HoloTextBox;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class ModuleGenerators extends GameModule {

    private static final double MID_GENERATOR_INTERVAL = 45;
    private static final double OUTER_GENERATOR_INTERVAL = 30;
    private static final double BASE_GENERATOR_IRON_INTERVAL = 3;
    private static final double BASE_GENERATOR_GOLD_INTERVAL = 15;

    private boolean enabled = false;

    private ArrayList<Generator> generators = new ArrayList<>();

    public ModuleGenerators(@NotNull GameHandler gameHandler, ArrayList<Location> midGens, ArrayList<Location> outerGens, ArrayList<Location> baseGens) {
        super(gameHandler, "GENERATORS");

        //Mid and outer generators
        for (Location outerGen : outerGens) {
            generators.add(new Generator(this, outerGen, ChatColor.AQUA + "Diamond Generator").setDisplayItem(new ItemStack(Material.DIAMOND_BLOCK))
                    .addDrop(Objects.requireNonNull(a(BedwarsItem.RESOURCE_DIAMOND.toString())), OUTER_GENERATOR_INTERVAL));
        }
        for (Location midGen : midGens) {
            generators.add(new Generator(this, midGen, ChatColor.GREEN + "Emerald Generator").setDisplayItem(new ItemStack(Material.EMERALD_BLOCK))
                    .addDrop(Objects.requireNonNull(a(BedwarsItem.RESOURCE_EMERALD.toString())), MID_GENERATOR_INTERVAL));
        }


        //Base generators
        for (Location baseGen : baseGens) {
            generators.add(new Generator(this, baseGen, ChatColor.GRAY + "Base Generator").setMultipleItemGiving(true)
                    .addDrop(Objects.requireNonNull(a(BedwarsItem.RESOURCE_IRON.toString())), BASE_GENERATOR_IRON_INTERVAL) //Drop 1 (Iron)
                    .addDrop(Objects.requireNonNull(a(BedwarsItem.RESOURCE_GOLD.toString())), BASE_GENERATOR_GOLD_INTERVAL)); //Drop 2 (Gold)

        }

        //Task interval of 1 tick or 0.05s
        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                if (!enabled) {
                    return;
                }
                if(++i == 12){
                    i = 0;
                }
                for (Generator generator : generators) {

                    if(i == 0){
                        if(generator.getName().equals(ChatColor.GRAY + "Base Generator")){
                            Location loc = generator.getLocation().clone().add(0.5, 0.2, 0.5);
                            loc.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, loc,2);
                        } else {
                            generator.spawnParticles();
                        }
                    }

                    HoloTextBox box = generator.getHoloTextBox();
                    //Decrement countdown
                    for (GeneratorDrop drops : generator.getDrops()) {
                        drops.countdown -= 0.05d;


                        //Check if it is time to drop an item
                        if (drops.countdown <= 0) {
                            drops.countdown = drops.getInterval(); //Reset countdown
                            generator.dropItem(generator.getDrops().indexOf(drops), 1); //Drop one
                        }
                        box.setLine(generator.getDrops().indexOf(drops), (generator.getDrops().size() > 1 ? new ItemBuilder(drops.getDrop()).getName() + " " : "") + ChatColor.YELLOW + Math.round(drops.countdown) + "s");
                    }

                    ArmorStand item = generator.getItem();
                    if (item != null) {
                        EulerAngle headPose = item.getHeadPose();
                        if (headPose.getY() <= 0) {
                            headPose = new EulerAngle(0, Math.toRadians(360), 0);
                        }
                        headPose = new EulerAngle(0, headPose.getY() - Math.toRadians(5), 0);
                        item.setHeadPose(headPose);
                    }

                }
            }
        }.runTaskTimer(CoreHandler.main, 0, 1);
    }

    private ItemStack a(String item) {
        GameItemHandler gameItemHandler = ((BedwarsHandler) this.getGameHandler()).getGameItemsModule().getGameItem(item);
        return gameItemHandler != null ? gameItemHandler.getItem(1) : null;
    }

    public ArrayList<Generator> getGenerators() {
        return this.generators;
    }

    public void setup(){
        //Stand setup
        generators.forEach(gen -> {
            if(gen.getName().equals(ChatColor.GRAY + "Base Generator")) return;
            HoloTextBox box = gen.getHoloTextBox();
            for (GeneratorDrop drops : gen.getDrops()) {
                box.addLine(new ItemBuilder(drops.getDrop()).getName() + " " + ChatColor.YELLOW + Math.round(drops.countdown) + "s");
                gen.dropItem(gen.getDrops().indexOf(drops), 1);
            }
            box.addLine(gen.getName());
            gen.createItem();
        });
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public void cleanup() {
        generators.forEach(gen -> {
            gen.getHoloTextBox().clear();
            gen.removeItem();
        });
    }

    public void clearGenerators() {
        generators.forEach(gen -> {
            gen.getHoloTextBox().clear();
            gen.removeItem();
        });
        generators.clear();
    }

    /**
     * Adds a generator
     *
     * @param generator   The generator to add
     * @param createItem  Whether or not to create a display-item
     */
    public void addGenerator(Generator generator, boolean createItem) {
        this.generators.add(generator);
        if (createItem) {
            generator.createItem();
        }
    }

    //Item pickup cancelling
    @EventSubscription
    private void onPickup(EntityPickupItemEvent event) {
        for (Generator gens : generators) {
            if (gens.getItem() != null) {
                if (gens.getItem().getUniqueId().equals(event.getItem().getUniqueId())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventSubscription
    private void onDespawn(ItemDespawnEvent event) {
        for (Generator gens : generators) {
            if (gens.getItem() != null) {
                if (gens.getItem().getUniqueId().equals(event.getEntity().getUniqueId())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

}
