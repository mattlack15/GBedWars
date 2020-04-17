package me.gravitinos.minigame.bedwars.game.module.gameitems;

import me.gravitinos.minigame.Sounds;
import me.gravitinos.minigame.gamecore.CoreHandler;
import me.gravitinos.minigame.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.minigame.gamecore.gameitem.SimpleGameItemHandler;
import me.gravitinos.minigame.gamecore.util.EventSubscription;
import me.gravitinos.minigame.gamecore.util.ItemBuilder;
import me.gravitinos.minigame.gamecore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

public class ItemReturnToSender extends SimpleGameItemHandler {

    public static int USES = 1;

    public static double AFFECTED_RADIUS = 3;

    public static final String IDENTIFIER_1 = ChatColor.YELLOW + "Uses: " + ChatColor.GRAY;
    public static final String IDENTIFIER_2 = ChatColor.AQUA + "Return to Sender";

    public static final String META_1 = "RTS_META_BOUNCED";


    public static String getMeta(UUID id) {
        return "RTS_META_" + id.toString();
    }

    public ItemReturnToSender(@NotNull ModuleGameItems module) {
        super(module, BedwarsItem.ITEM_RETURN_TO_SENDER.toString(), IDENTIFIER_2, IDENTIFIER_1);
    }

    private int taskId = -1;

    @Override
    public void onEnable() {
        taskId = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (isMatch(p.getInventory().getItemInMainHand())) {
                        p.removePotionEffect(PotionEffectType.SLOW);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 1, false, true));
                        for (Entity entity : p.getNearbyEntities(AFFECTED_RADIUS + 5, AFFECTED_RADIUS + 5, AFFECTED_RADIUS + 5)) {
                            if(entity.getLocation().add(entity.getVelocity()).distanceSquared(p.getEyeLocation()) > Math.pow(AFFECTED_RADIUS, 2))
                                continue;
                            if (entity instanceof Arrow && !((Arrow)entity).isInBlock()) {
                                if (entity.hasMetadata(getMeta(p.getUniqueId())) || (((Arrow) entity).getShooter() instanceof Player && ((Player) ((Arrow) entity).getShooter()).getUniqueId().equals(p.getUniqueId())))
                                    continue;
                                entity.setVelocity(entity.getVelocity().multiply(-1));
                                entity.setMetadata(getMeta(p.getUniqueId()), new FixedMetadataValue(CoreHandler.main, null));
                                entity.setMetadata(META_1, new FixedMetadataValue(CoreHandler.main, null));

                                //Particles
                                entity.getWorld().spawnParticle(Particle.BLOCK_CRACK, entity.getLocation(), 20, 0.2, 0.2, 0.2, 0.05, Material.STAINED_GLASS.getNewData((byte) 3));

                                ItemStack stack = p.getInventory().getItemInMainHand().clone();

                                int uses = getUsesLeft(stack);

                                if(stack.getAmount() > 1 && uses > 1){
                                    ItemStack stack1 = stack.clone();
                                    stack1.setAmount(stack1.getAmount()-1);
                                    int i = p.getInventory().firstEmpty();
                                    if(i == -1){
                                        p.getWorld().dropItemNaturally(p.getLocation(), stack1);
                                    } else {
                                        p.getInventory().setItem(i, stack1);
                                    }
                                }
                                stack.setAmount(1);
                                if(uses-1 == 0){
                                    p.getInventory().setItemInMainHand(null);
                                    p.playSound(p.getLocation(), Sounds.SOUND_ITEM_BREAK, 1f, 1f);
                                } else {
                                    stack = getItem(uses-1);
                                    p.getInventory().setItemInMainHand(stack);
                                }
                                p.getWorld().playSound(entity.getLocation(), Sounds.SOUND_ANVIL_PLACE, 0.8f, 1.4f);
                            }
                        }
                    }
                }

            }
        }.runTaskTimer(CoreHandler.main, 0, 1).getTaskId();
    }

    public int getUsesLeft(ItemStack stack){
        return getLevel(stack);
        //        ItemBuilder builder = new ItemBuilder(stack);
//
//        if(builder.getLore().size() < 1)
//            return -1;
//
//        String str = builder.getLore().get(0);
//        int index = str.indexOf(IDENTIFIER_1);
//
//        if(index == -1 || index + IDENTIFIER_1.length() >= str.length())
//            return -1;
//
//        String str1 = str.substring(index + IDENTIFIER_1.length());
//        try{
//            return Integer.parseInt(str1);
//        } catch(Exception e){
//            return -1;
//        }
    }

    @Override
    public void onDisable() {
        if (taskId != -1)
            Bukkit.getScheduler().cancelTask(taskId);
    }

    @Override
    public String getDescription() {
        return "Holding this will send arrows shot at you back to their shooter";
    }

    @Override
    public ItemStack getItem(int level) {
        ItemBuilder builder = new ItemBuilder(Material.SLIME_BALL, 1).setName(IDENTIFIER_2);
        builder.addGlow();
        if(level != 1) {
            builder.addLore(IDENTIFIER_1 + level);
        }
        for (String lines : TextUtil.splitIntoLines(getDescription(), 30)) {
            builder.addLore(ChatColor.GRAY + lines);
        }
        return builder.build();
    }

    @EventSubscription
    private void onSwitch(PlayerItemHeldEvent event){
        if(this.isMatch(event.getPlayer().getInventory().getItem(event.getPreviousSlot()))){
            event.getPlayer().removePotionEffect(PotionEffectType.SLOW);
        }
    }
}
