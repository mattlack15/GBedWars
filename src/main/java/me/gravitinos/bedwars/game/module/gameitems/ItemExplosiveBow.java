package me.gravitinos.bedwars.game.module.gameitems;

import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.game.module.ModuleGameEnvironment;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.bedwars.gamecore.gameitem.SimpleGameItemHandler;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import me.gravitinos.bedwars.gamecore.util.TextUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemExplosiveBow extends SimpleGameItemHandler {

    public static final int COOLDOWN_SECONDS = 5;

    public static final String META_VAL = "BW_EXPLOSIVE_ARROW";

    private Map<UUID, Long> justBroke = new HashMap<>();

    private Map<UUID, Long> lastUsed = new HashMap<>();


    public ItemExplosiveBow(@NotNull ModuleGameItems module) {
        super(module, BedwarsItem.ITEM_EXPLOSIVE_BOW.toString());
    }

    @Override
    public String getDescription() {
        return "Arrows fired from this bow explode when they hit something";
    }

    @Override
    public ItemStack getItem(int level) {
        ItemBuilder builder = new ItemBuilder(Material.BOW, 1).setName(ChatColor.DARK_RED + "Explosive Bow");
        for(String lines :  TextUtil.splitIntoLines(this.getDescription(), 25)){
            builder.addLore(ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', lines));
        }
        builder.addGlow();
        builder.setDurabilityLeft((short)3);
        return builder.build();
    }

    private ArrayList<UUID> fallingBlocks = new ArrayList<>();

    @EventSubscription
    private void onFallingBlockLand(EntityChangeBlockEvent event){
        if(this.fallingBlocks.contains(event.getEntity().getUniqueId())){
            this.fallingBlocks.remove(event.getEntity().getUniqueId());
            event.setCancelled(true);
        }
    }

    @EventSubscription
    private void onDamage(PlayerItemDamageEvent event){
        if(event.getPlayer() == null || event.getItem() == null){
            return;
        }
        if(isMatch(event.getItem())){
            if(this.lastUsed.containsKey(event.getPlayer().getUniqueId())) {
                long timeSinceLastUse = System.currentTimeMillis() - this.lastUsed.get(event.getPlayer().getUniqueId());
                if (timeSinceLastUse < COOLDOWN_SECONDS * 1000) {
                    event.setCancelled(true);
                    return;
                }
            }
            if(event.getItem().getDurability() + event.getDamage() > event.getItem().getType().getMaxDurability()){ //Looks like a weird comparison but durability works weirdly
                justBroke.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
            }
        }
    }

    @EventSubscription
    private void onLand(ProjectileHitEvent event){
        if(event.getEntity().hasMetadata(META_VAL)){
            if(!getModule().getGameHandler().isRunning()){
                event.getEntity().setShooter(null);
                event.getEntity().remove();
                return;
            }

            this.fallingBlocks.addAll(createExplosion(event.getEntity().getLocation(), (int) 3.5, (Entity) event.getEntity().getShooter()));

            event.getEntity().remove();

        }
    }

    public static ArrayList<UUID> createExplosion(Location center, int radius, Entity damager){
        Random rand = new Random(System.currentTimeMillis());
        ArrayList<UUID> fallingBlocks = new ArrayList<>();

        center.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, center, 1);
        center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);

        for(Entity ents : center.getWorld().getNearbyEntities(center, 4, 4, 4)){
            if(ents instanceof Player){
                if(((Player) ents).getGameMode().equals(GameMode.CREATIVE)){
                    continue;
                }
                EntityDamageEvent ev = new EntityDamageByEntityEvent(damager, ents, EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, 16 - ents.getLocation().distance(center));
                Bukkit.getPluginManager().callEvent(ev);
                if(!ev.isCancelled()) {
                    ((Player) ents).setHealth(((Player) ents).getHealth() - ev.getFinalDamage());
                }
                ents.setVelocity(((Player) ents).getEyeLocation().toVector().subtract(center.toVector().add(new Vector(0, -1, 0))).normalize().multiply(0.65).add(new Vector(0, 0.2, 0)));
            }
        }

        ArrayList<Block> states1 = getBlocksSphere(center, 4);

        for(Block states : states1){
            if(states.getType().equals(Material.BED_BLOCK)){
                continue;
            }
            if(states.hasMetadata(ModuleGameEnvironment.PLAYER_PLACED_BLOCK_META)) {

                Location blockLoc = states.getLocation().add(0.5, 0.5, 0.5);

                double distance = blockLoc.distance(center);

                if(states.getType().equals(Material.OBSIDIAN) && distance > 1) {
                    continue;
                } else if(states.getType().equals(Material.ENDER_STONE) && distance > 1.8){
                    continue;
                }

                if (rand.nextInt(100) < 40) {
                    FallingBlock block = states.getLocation().getWorld().spawnFallingBlock(states.getLocation(), states.getState().getData());
                    block.setDropItem(false);
                    block.setInvulnerable(true);
                    block.setVelocity(block.getLocation().toVector().subtract(center.toVector().add(new Vector(0, -1.8, 0))).normalize().multiply(0.5).add(new Vector(0, 0.2, 0)));
                    fallingBlocks.add(block.getUniqueId());
                }

                states.setType(Material.AIR);
            }
        }
        return fallingBlocks;
    }

    public static ArrayList<Block> getBlocksSphere(Location loc, int radius){
        ArrayList<Block> states = new ArrayList<>();
        Location loc1 = loc.clone().subtract(radius, radius, radius);
        Location loc2 = loc.clone().add(radius, radius, radius);
        for(int x = loc1.getBlockX(); x < loc2.getBlockX(); x++){
            for(int y = loc1.getBlockY(); y < loc2.getBlockY(); y++){
                for(int z = loc1.getBlockZ(); z < loc2.getBlockZ(); z++){
                    Location loc3 = new Location(loc1.getWorld(), x, y, z);
                    if(loc3.distanceSquared(loc) <= Math.pow(radius, 2)) {
                        states.add(loc3.getBlock());
                    }
                }
            }
        }
        return states;
    }

    @EventSubscription
    private void onShoot(ProjectileLaunchEvent event){
        if(event.getEntity().getShooter() instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) event.getEntity().getShooter();
            if (getModule().getGameHandler().isPlaying(livingEntity.getUniqueId())) {
                if (livingEntity.getEquipment().getItemInMainHand() != null) {
                    if (this.isMatch(livingEntity.getEquipment().getItemInMainHand()) || (this.justBroke.containsKey(livingEntity.getUniqueId()) &&
                            System.currentTimeMillis() - this.justBroke.get(livingEntity.getUniqueId()) < 50/*1 tick*/)) {
                        if(this.lastUsed.containsKey(livingEntity.getUniqueId())){
                            long timeSinceLastUse = System.currentTimeMillis() - this.lastUsed.get(livingEntity.getUniqueId());
                            if(timeSinceLastUse < COOLDOWN_SECONDS * 1000){
                                event.setCancelled(true);
                                ((BedwarsHandler)getModule().getGameHandler()).sendGameMessage(livingEntity,ChatColor.GRAY + "You must wait 5 seconds before firing another explosive arrow!", "Game");
                                return;
                            }
                        }
                        this.lastUsed.put(livingEntity.getUniqueId(), System.currentTimeMillis());
                        event.getEntity().setMetadata(META_VAL, new FixedMetadataValue(CoreHandler.main, null));
                    }
                }
            }
        }
    }
}
