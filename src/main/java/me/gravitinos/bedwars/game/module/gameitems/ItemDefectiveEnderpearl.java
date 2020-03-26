package me.gravitinos.bedwars.game.module.gameitems;

import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.bedwars.gamecore.gameitem.SimpleGameItemHandler;
import me.gravitinos.bedwars.gamecore.util.ActionBar;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import me.gravitinos.bedwars.gamecore.util.TextUtil;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
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
            Location loc = locs.get(player.getUniqueId());
            locs.remove(player.getUniqueId());
            UUID id = player.getUniqueId();

            new BukkitRunnable() {
                int ticks = TICKS_DEFECTIVE;

                @Override
                public void run() {
                    if (Bukkit.getPlayer(id) == null || !isEnabled()) {
                        Bukkit.getScheduler().cancelTask(this.getTaskId());
                        return;
                    }
                    Player p = Bukkit.getPlayer(id);
                    if (ticks <= 0) {
                        p.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                        p.setFallDistance(0);
                        Bukkit.getScheduler().cancelTask(this.getTaskId());
                        return;
                    } else {
                        ActionBar.send(p, TextUtil.getProgressBar(1 - ticks / (double) TICKS_DEFECTIVE, 20, "|", ChatColor.GREEN + "", ChatColor.WHITE + "")
                                + " &c" + (int) Math.ceil(ticks / 20d) + "s");
                        if(ticks % 20 == 0){
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_SNARE, 4f, 1f);
                        }
                    }
                    ticks -= 2;
                }
            }.runTaskTimer(CoreHandler.main, 0, 2);
        }
    }

    @EventSubscription
    private void onThrow(ProjectileLaunchEvent event) {
        if (!isEnabled()) {
            return;
        }
        if (event.getEntity().getShooter() instanceof LivingEntity) {
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
