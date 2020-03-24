package me.gravitinos.bedwars.game.module.gameitems;

import me.gravitinos.bedwars.gamecore.gameitem.GameItemHandler;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.bedwars.gamecore.gameitem.SimpleGameItemHandler;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemTnt extends SimpleGameItemHandler {
    public ItemTnt(@NotNull ModuleGameItems module) {
        super(module, BedwarsItem.ITEM_TNT.toString());
    }

    @Override
    public String getDescription() {
        return "Right Click to Prime and Drop, Left Click to Prime and Throw";
    }

    @Override
    public ItemStack getItem(int level) {
        ItemBuilder builder = new ItemBuilder(Material.TNT, 1);
        builder.setName("&c&lTNT");
        builder.addLore("&eRight Click &7to Prime and Drop");
        builder.addLore("&eLeft Click &7to Prime and Throw");
        return builder.build();
    }

    private Map<UUID, UUID> tnts = new HashMap<>();

    @EventSubscription
    private void interact(PlayerInteractEvent event){
        if(event.getPlayer().getInventory().getItemInMainHand() == null || !this.isMatch(event.getPlayer().getInventory().getItemInMainHand())){
            return;
        }

        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)){
            //Drop
            Entity e = event.getPlayer().getWorld().spawnEntity(event.getPlayer().getEyeLocation(), EntityType.PRIMED_TNT);
            tnts.put(e.getUniqueId(), event.getPlayer().getUniqueId());

            //Remove from player's hand
            if(event.getItem().getAmount() == 1) {
                event.getPlayer().getInventory().setItemInMainHand(null);
            } else {
                event.getItem().setAmount(event.getItem().getAmount() - 1);
            }

        } else if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
            //Throw

            TNTPrimed tntPrimed = (TNTPrimed) event.getPlayer().getWorld().spawnEntity(event.getPlayer().getEyeLocation(), EntityType.PRIMED_TNT);
            tntPrimed.setVelocity(event.getPlayer().getLocation().getDirection().multiply(0.75));
            tnts.put(tntPrimed.getUniqueId(), event.getPlayer().getUniqueId());

            //Remove from player's hand
            if(event.getItem().getAmount() == 1) {
                event.getPlayer().getInventory().setItemInMainHand(null);
            } else {
                event.getItem().setAmount(event.getItem().getAmount() - 1);
            }
        }
    }

    private ArrayList<UUID> fallingBlocks = new ArrayList<>();

    @EventSubscription
    private void onExplode(EntityExplodeEvent event){
        if(tnts.containsKey(event.getEntity().getUniqueId())) {
            event.blockList().clear();
            UUID id = tnts.get(event.getEntity().getUniqueId());
            if(Bukkit.getPlayer(id) != null) {
                fallingBlocks.addAll(ItemExplosiveBow.createExplosion(event.getEntity().getLocation(), 3, Bukkit.getPlayer(id)));
            } else {
                fallingBlocks.addAll(ItemExplosiveBow.createExplosion(event.getEntity().getLocation(), 3, event.getEntity()));
            }
            event.setYield(0);
            event.setCancelled(true);
        }
    }

    @EventSubscription
    public void onPlace(BlockPlaceEvent event){
        if (event.getItemInHand() != null && this.isMatch(event.getItemInHand())) {
            event.setCancelled(true);
        }
    }

    @EventSubscription
    private void onSolidify(EntityChangeBlockEvent event){
        if(fallingBlocks.contains(event.getEntity().getUniqueId())){
            fallingBlocks.remove(event.getEntity().getUniqueId());
            event.setCancelled(true);
        }
    }
}
