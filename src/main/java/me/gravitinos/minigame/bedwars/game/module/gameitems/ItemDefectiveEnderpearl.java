package me.gravitinos.minigame.bedwars.game.module.gameitems;

import me.gravitinos.minigame.MinigameMessenger;
import me.gravitinos.minigame.SpigotMinigames;
import me.gravitinos.minigame.bedwars.game.BedwarsHandler;
import me.gravitinos.minigame.gamecore.CoreHandler;
import me.gravitinos.minigame.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.minigame.gamecore.gameitem.SimpleGameItemHandler;
import me.gravitinos.minigame.gamecore.util.ActionBar;
import me.gravitinos.minigame.gamecore.util.EventSubscription;
import me.gravitinos.minigame.gamecore.util.ItemBuilder;
import me.gravitinos.minigame.gamecore.util.TextUtil;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemDefectiveEnderpearl extends SimpleGameItemHandler {

    private static final String META_VAL = "BW_DEFECTIVE_EPEARL";
    private static final int TICKS_DEFECTIVE = 120;

    public ItemDefectiveEnderpearl(@NotNull ModuleGameItems module) {
        super(module, BedwarsItem.ITEM_DEFECTIVE_ENDERPEARL.name());
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        this.locs.clear();
    }

    @Override
    public String getDescription() {
        return "Teleports you back to your starting place after 6 seconds";
    }

    @Override
    public ItemStack getItem(int level) {
        ItemBuilder builder = new ItemBuilder(Material.ENDER_PEARL, 1).setName("&dDefective Enderpearl");
        for (String lines : TextUtil.splitIntoLines(this.getDescription(), 25)) {
            builder.addLore(ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', lines));
        }
        builder.addGlow();
        return builder.build();
    }

    private Map<UUID, Location> locs = new HashMap<>();

    @EventSubscription
    private void onLand(ProjectileHitEvent event) {
        if (event.getEntity().hasMetadata(META_VAL)) {
            if (!getModule().getGameHandler().isRunning()) {
                event.getEntity().setShooter(null);
                event.getEntity().remove();
                return;
            }

            Player player = (Player) event.getEntity().getShooter();

            if(!locs.containsKey(player.getUniqueId()))
                return;

            Location loc = locs.get(player.getUniqueId());
            UUID id = player.getUniqueId();

            new BukkitRunnable() {
                int ticks = TICKS_DEFECTIVE;
                int interval = 26;

                @Override
                public void run() {
                    if (Bukkit.getPlayer(id) == null || !isEnabled() || !getModule().getGameHandler().isPlaying(id) || ((BedwarsHandler)getModule().getGameHandler()).isRespawning(id)) {
                        Bukkit.getScheduler().cancelTask(this.getTaskId());
                        locs.remove(id);
                        return;
                    }
                    Player p = Bukkit.getPlayer(id);
                    if(ticks % (interval*2) == 0){
                        if(interval != 1) {
                            interval-=5;
                        }
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 0.6f, 2f);
                    }
                    if (ticks <= 0) {
                        Bukkit.getScheduler().cancelTask(this.getTaskId());
                        locs.remove(id);

                        loc.setDirection(p.getLocation().getDirection());
                        Vector vec = p.getVelocity();
                        p.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                        p.setVelocity(vec);
                        p.setFallDistance(0);

                        return;
                    } else {
                        ActionBar.send(p, TextUtil.getProgressBar(1 - ticks / (double) TICKS_DEFECTIVE, 20, "|", ChatColor.GREEN + "", ChatColor.WHITE + "")
                                + " &c" + (int) Math.ceil(ticks / 20d) + "s");
                    }
                    ticks -= 2;
                }
            }.runTaskTimer(CoreHandler.main, 0, 2);
        }
    }

    @EventSubscription
    private void onInteract(PlayerInteractEvent event){
        if (!isEnabled()) {
            return;
        }
        if(getModule().getGameHandler().isPlaying(event.getPlayer().getUniqueId())){
            if (event.getPlayer().getEquipment().getItemInMainHand() != null) {
                if (this.isMatch(event.getPlayer().getEquipment().getItemInMainHand())) {
                    if(this.locs.containsKey(event.getPlayer().getUniqueId())){
                        MinigameMessenger.msgPlayerGame(event.getPlayer(), "You may not use this yet!", "Game");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventSubscription
    private void onThrow(ProjectileLaunchEvent event) {
        if (!isEnabled()) {
            return;
        }
        if (event.getEntity().getShooter() instanceof Player) {
            LivingEntity livingEntity = (LivingEntity) event.getEntity().getShooter();
            if (getModule().getGameHandler().isPlaying(livingEntity.getUniqueId())) {
                if (livingEntity.getEquipment().getItemInMainHand() != null) {
                    if (this.isMatch(livingEntity.getEquipment().getItemInMainHand())) {
                        event.getEntity().setMetadata(META_VAL, new FixedMetadataValue(CoreHandler.main, ""));
                        locs.put(livingEntity.getUniqueId(), livingEntity.getLocation());
                    }
                }
            }
        }
    }
}
