package me.gravitinos.minigame.bedwars.game.module.generator;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.gravitinos.minigame.bedwars.game.module.ModuleGenerators;
import me.gravitinos.minigame.gamecore.util.ArmorStandFactory;
import me.gravitinos.minigame.gamecore.util.EntityStore;
import me.gravitinos.minigame.gamecore.util.HoloTextBox;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Generator {
    private String name;
    private Location location;
    private HoloTextBox holoTextBox;
    private EntityStore<ArmorStand> item = new EntityStore<>(null);
    private ItemStack displayItem = null;
    private ArrayList<GeneratorDrop> drops = new ArrayList<>();
    private boolean multipleItemGiving = false;
    private ModuleGenerators moduleGenerators;
    private int maxHeldItems = 8;

    public Generator(/*Removable for other plugins*/@NotNull ModuleGenerators module, @NotNull Location location, @NotNull String name) {
        this.name = name;
        this.holoTextBox = new HoloTextBox(location.clone().add(0.5, 2.4, 0.5), 0.3, true);
        this.location = location;
        this.moduleGenerators = module;
    }

    public int getMaxHeldItems(){
        return this.maxHeldItems;
    }

    public void setMaxHeldItems(int maxHeldItems){
        this.maxHeldItems = maxHeldItems;
    }

    public Generator addDrop(@NotNull ItemStack drop, double interval){
        this.drops.add(new GeneratorDrop(drop, interval));
        return this;
    }

    public ArrayList<GeneratorDrop> getDrops(){
        return this.drops;
    }

    public GeneratorDrop getDrop(int dropIndex){
        if(dropIndex >= drops.size()){
            return null;
        }
        return this.drops.get(dropIndex);
    }

    public void spawnParticles(){


        ProtocolManager pm = ProtocolLibrary.getProtocolManager();

        double radius = 0.3;

        for(double i = 0; i < 2.4; i+=0.4){
            Location center = this.location.clone().add(0.5, i, 0.5);
            for(double radians = 0; radians < Math.toRadians(360); radians += Math.toRadians(360) / 10d){
                double x = center.getX() + radius * Math.cos(radians);
                double z = center.getZ() + radius * Math.sin(radians);
                Location loc = new Location(center.getWorld(), x, center.getY(), z);
                PacketContainer packetContainer = pm.createPacket(PacketType.Play.Server.WORLD_PARTICLES);
                packetContainer.getModifier().writeDefaults();
                packetContainer.getParticles().write(0, EnumWrappers.Particle.ENCHANTMENT_TABLE);
                packetContainer.getFloat().write(0, (float)x).write(1, (float)center.getY()).write(2, (float)z);

                for(Player player : Bukkit.getOnlinePlayers()){
                    try {
                        pm.sendServerPacket(player, packetContainer);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public int getHeldItems(){
        int i = 0;
        for(Entity ents : location.getWorld().getNearbyEntities(location.clone().add(0.5, 0.5, 0.5), 1.5, 4, 1.5)){
            if(ents instanceof Item){
                for(GeneratorDrop drops : this.getDrops()){
                    if(drops.getDrop().isSimilar(((Item) ents).getItemStack())){
                        i += ((Item) ents).getItemStack().getAmount();
                        break;
                    }
                }
            }
        }
        return i;
    }

    public Generator setDisplayItem(@NotNull ItemStack stack){
        this.displayItem = stack;
        ArmorStand stand = this.getItem();
        if(stand != null){
            stand.setHelmet(stack);
        }
        return this;
    }

    public boolean isMultipleItemGiving() {
        return multipleItemGiving;
    }

    public Generator setMultipleItemGiving(boolean multipleItemGiving) {
        this.multipleItemGiving = multipleItemGiving;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Location getLocation() {
        return this.location;
    }

    public void dropItem(int dropIndex, int amount) {
        if(dropIndex >= this.drops.size()){
            return;
        }
        GeneratorDrop genDrop = this.drops.get(dropIndex);
        ItemStack stack = genDrop.getDrop();
        stack.setAmount(amount);
        boolean playerFound = false;
        if(multipleItemGiving) {
            for (Entity ents : location.getWorld().getNearbyEntities(location.clone().add(0.5, 0, 0.5), 1.6, 2, 1.6)) {
                if (ents instanceof Player && moduleGenerators.getGameHandler().isPlaying(ents.getUniqueId())) {
                    playerFound = true;
                    Player p = (Player) ents;
                    p.getInventory().addItem(stack);
                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 2f, 1f);
                }
            }
        }
        if (!playerFound) {
            this.location.getWorld().dropItem(location.clone().add(0.5, 0.05, 0.5), stack).setVelocity(new Vector(0, 0, 0));
        }
    }

    //Item

    public void createItem() {
        this.removeItem();
        Location loc = location.clone().add(0.5, 2.25, 0.5);
        ArmorStand item1 = ArmorStandFactory.createHidden(loc);
        item1.setGravity(false);
        item1.setHelmet(this.displayItem);
        item1.setVelocity(new Vector(0, 0, 0));
        item1.setInvulnerable(true);
        item1.setSmall(false);
        this.item = new EntityStore<>(item1);
    }

    public ArmorStand getItem() {
        return this.item.getEntity();
    }

    public void removeItem() {
        ArmorStand stand = this.item.getEntity();
        if (stand != null) {
            stand.remove();
        }
    }

    //Text

    /**
     * Get the holo text box
     */
    public HoloTextBox getHoloTextBox(){
        return this.holoTextBox;
    }
}
