package me.gravitinos.minigame.bedwars.game.module;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.gravitinos.minigame.bedwars.game.BedwarsHandler;
import me.gravitinos.minigame.bedwars.game.module.damage.DamageType;
import me.gravitinos.minigame.bedwars.game.module.damage.DeathType;
import me.gravitinos.minigame.bedwars.game.module.damage.LastDamage;
import me.gravitinos.minigame.bedwars.game.module.damage.LastDamageList;
import me.gravitinos.minigame.bedwars.game.module.gameitems.ItemInvisPot;
import me.gravitinos.minigame.gamecore.CoreHandler;
import me.gravitinos.minigame.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.minigame.gamecore.module.GameHandler;
import me.gravitinos.minigame.gamecore.module.GameModule;
import me.gravitinos.minigame.gamecore.util.EventSubscription;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModuleDamageHandler extends GameModule {

    private Map<UUID, LastDamageList> lastDamageMap = new HashMap<>();

    public ArrayList<UUID> allowSound = new ArrayList<>();

    private PacketAdapter packetAdapter = new PacketAdapter(CoreHandler.main, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
        @Override
        public void onPacketSending(PacketEvent event) {
            if(!ModuleDamageHandler.this.getGameHandler().isRunning()){
                return;
            }
            PacketContainer container = event.getPacket();
            Sound sound = container.getSoundEffects().read(0);

            if(ModuleDamageHandler.this.allowSound.contains(event.getPlayer().getUniqueId())){
                ModuleDamageHandler.this.allowSound.remove(event.getPlayer().getUniqueId());
                return;
            }

            if(sound.equals(Sound.ENTITY_PLAYER_HURT) || sound.name().startsWith("ENTITY_PLAYER_ATTACK_")) {
                event.setCancelled(true);
            }
        }
    };

    public ModuleDamageHandler() {
        super("DAMAGE_HANDLER");
    }

    @Override
    public void enable() {
        super.enable();
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        pm.addPacketListener(packetAdapter);
    }

    @Override
    public void disable() {
        super.disable();
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        pm.removePacketListener(packetAdapter);
    }

    @EventSubscription
    private void onDeath(EntityDeathEvent event){
        if (getGameHandler().isPlaying(event.getEntity().getUniqueId()) || getGameHandler().isSpectating(event.getEntity().getUniqueId())) {
            if(event.getEntity() instanceof Player){
                ((Player) event.getEntity()).spigot().respawn();
            }
        }
    }

    @EventSubscription
    private void onDamage(EntityDamageEvent event) {
        if(event.isCancelled()){
            return;
        }
        if (getGameHandler().isSpectating(event.getEntity().getUniqueId()) || ((BedwarsHandler) getGameHandler()).isRespawning(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        if (getGameHandler().isPlaying(event.getEntity().getUniqueId())) {

            String cause = event.getCause().name().toLowerCase();
            String with = null;
            Player by = null;

            if (event instanceof EntityDamageByEntityEvent) { //Instance of EntityDamageByEntityEvent
                EntityDamageByEntityEvent e1 = (EntityDamageByEntityEvent) event;
                if (!getGameHandler().isPlaying(e1.getDamager().getUniqueId()) || ((BedwarsHandler) getGameHandler()).isRespawning(e1.getDamager().getUniqueId())) {
                    if (e1.getDamager() instanceof Projectile) {
                        ProjectileSource source = ((Projectile) e1.getDamager()).getShooter();
                        if (source instanceof LivingEntity) {
                            if (!getGameHandler().isPlaying(((LivingEntity) source).getUniqueId())) {
                                event.setCancelled(true);
                                return;
                            }
                            cause = ((LivingEntity) source).getName();
                        }
                    } else {
                        event.setCancelled(true);
                        return;
                    }
                }
                if (e1.getDamager() instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) e1.getDamager();

                    BedwarsHandler handler = (BedwarsHandler) getGameHandler();
                    if (handler.getTeamManagerModule().getTeam(e1.getEntity().getUniqueId()) != null &&
                            handler.getTeamManagerModule().getTeam(e1.getEntity().getUniqueId()).equals(handler.getTeamManagerModule().getTeam(e1.getDamager().getUniqueId()))) {
                        event.setCancelled(true);
                        return;
                    }
                    cause = e1.getDamager().getName(); //Set cause (by) to damager's name
                    if (livingEntity.getEquipment().getItemInMainHand().hasItemMeta()) {
                        with = livingEntity.getEquipment().getItemInMainHand().getItemMeta().getDisplayName(); //Set with to the name of the entity's item in main hand
                    }
                }
                if (e1.getDamager() instanceof Player) {
                    by = (Player) e1.getDamager();
                    BedwarsHandler handler = (BedwarsHandler) getGameHandler();
                    handler.getModule(ModuleGameItems.class).getGameItem(ItemInvisPot.class).removeInvisible(((Player) e1.getDamager()));
                }
            }

            if (!(event.getEntity() instanceof Player)) {
                return;
            }

            Player player = (Player) event.getEntity();

            //Hit sound
            ItemStack[] armor = player.getInventory().getArmorContents();
            int leather = 0;
            int none = 0;
            for (ItemStack stack : armor) {
                if (stack == null) {
                    none++;
                } else if (stack.getItemMeta() instanceof LeatherArmorMeta) {
                    leather++;
                }
            }
            if(event instanceof EntityDamageByEntityEvent) {
                if (leather > 2) {
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SPLASH_POTION_THROW, 0.8f,
                            2f);
                } else if (leather + none <= 2) {
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.8f, 1.5f);
                }
            }


            BedwarsHandler handler = (BedwarsHandler) getGameHandler();
            handler.getModule(ModuleGameItems.class).getGameItem(ItemInvisPot.class).removeInvisible(player);


            LastDamageList lastDamageList = lastDamageMap.get(player.getUniqueId());
            if (lastDamageList == null) {
                lastDamageMap.put(player.getUniqueId(), new LastDamageList());
                lastDamageList = lastDamageMap.get(player.getUniqueId());
            }
            lastDamageList.removeDamagesPast(8000); //Remove last damages from over 8 seconds ago

            DamageType damageType = DamageType.PVP;
            if (event.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) ||
                    event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
                damageType = DamageType.EXPLOSION;
            } else if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                damageType = DamageType.FALL;
            } else if (event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                damageType = DamageType.PROJECTILE;
            } else if (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) ||
                    event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) ||
                    event.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
                damageType = DamageType.FIRE;
            }

            LastDamage ld = new LastDamage(cause, damageType, event.getFinalDamage(), System.currentTimeMillis());
            lastDamageList.addLastDamage(ld);

            if (player.getHealth() - event.getFinalDamage() <= 0) {
                //Player died
                event.setCancelled(true);

                DeathType deathType = ld.getDamageType().getDeathType();

                String damager = ld.getDamager();
                if (ld.getDamageType().equals(DamageType.BORDER) && lastDamageList.getLastDamages().size() > 1) {
                    deathType = lastDamageList.getLastDamages().get(1).getDamageType().getDeathType();
                    damager = lastDamageList.getLastDamages().get(1).getDamager();
                }

                if (by != null) {
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 2f, 1f);
                }
                lastDamageList.clear();

                ((BedwarsHandler) getGameHandler()).killPlayer(player.getUniqueId(), deathType, damager + (with != null ? " &7with&e " + with : ""));
            }
        }
    }

    public LastDamageList getLastDamageList(UUID player) {
        if (!this.lastDamageMap.containsKey(player)) {
            this.lastDamageMap.put(player, new LastDamageList());
        }
        return this.lastDamageMap.get(player);
    }
}
