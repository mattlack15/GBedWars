package me.gravitinos.bedwars.game.module.gameitems;

import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.game.module.ModuleGameEnvironment;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.bedwars.gamecore.gameitem.SimpleGameItemHandler;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import me.gravitinos.bedwars.gamecore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ItemBridgeEgg extends SimpleGameItemHandler {

    private static final int LIFE_TICKS = 25;

    public ItemBridgeEgg(@NotNull ModuleGameItems module) {
        super(module, BedwarsItem.ITEM_BRIDGE_EGG.name());
    }

    @Override
    public String getDescription() {
        return "Builds a bridge to the other side";
    }

    @Override
    public ItemStack getItem(int level) {
        ItemBuilder builder = new ItemBuilder(Material.EGG, 1).setName("&eBridge Egg");
        for (String lines : TextUtil.splitIntoLines(this.getDescription(), 25)) {
            builder.addLore(ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', lines));
        }
        return builder.build();
    }

    @EventSubscription
    private void onLand(PlayerEggThrowEvent event){
        if(isEnabled()){
            event.setHatching(false);
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
                    if (event.getEntity() instanceof Egg) {

                        if (!(livingEntity instanceof Player)) {
                            return;
                        }

                        Egg egg = (Egg) event.getEntity();
                        egg.setInvulnerable(true);
                        egg.setSilent(true);

                        double yVal = Math.floor(livingEntity.getLocation().getY() - 1);

                        Material material = Material.WOOL;
                        byte data = ((BedwarsHandler) getModule().getGameHandler()).getPlayerInfo(livingEntity.getUniqueId()).getTeam().getWoolColour();

                        new BukkitRunnable() {
                            private ArrayList<Location> locs = new ArrayList<>();

                            private int ticks = LIFE_TICKS;

                            private Location lastLocation = livingEntity.getLocation();

                            @Override
                            public void run() {
                                if (ticks-- <= 0 || !isEnabled()) {
                                    Bukkit.getScheduler().cancelTask(this.getTaskId());
                                    egg.remove();
                                    Bukkit.broadcastMessage("Egg time ran out");
                                    return;
                                }
                                Vector dir = egg.getLocation().subtract(lastLocation).toVector();
                                for (double i = 0; Math.pow(i, 2) <= lastLocation.distanceSquared(egg.getLocation())+1; i += 0.5) {
                                    Location loc = lastLocation.clone().add(dir.clone().multiply(i));
                                    loc.setY(yVal);
                                    Block block = loc.getBlock();
                                    loc = block.getLocation();
                                    if(((BedwarsHandler)getModule().getGameHandler()).getBlockHandlerModule().isInRestrictedBuildingArea(loc)){
                                        continue;
                                    }
                                    if (!this.locs.contains(loc)) {
                                        if(!block.getType().equals(Material.AIR)){
                                            continue;
                                        }
                                        locs.add(loc);
                                        getModule().getGameHandler().getModule(ModuleGameEnvironment.class).addRevertingBlockState(block.getState());
                                        block.setType(material);
                                        block.setData(data);
                                        block.setMetadata(ModuleGameEnvironment.PLAYER_PLACED_BLOCK_META, new FixedMetadataValue(CoreHandler.main, ""));
                                    }
                                }
                                lastLocation = egg.getLocation();
                            }
                        }.runTaskTimer(CoreHandler.main, 0, 1);
                    }
                }
            }
        }
    }
}
