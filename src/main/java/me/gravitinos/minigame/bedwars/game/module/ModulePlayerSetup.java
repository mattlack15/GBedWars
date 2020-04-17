package me.gravitinos.minigame.bedwars.game.module;

import com.google.common.collect.Lists;
import me.gravitinos.minigame.bedwars.game.BedwarsHandler;
import me.gravitinos.minigame.bedwars.game.BedwarsTeam;
import me.gravitinos.minigame.bedwars.game.info.PermanentArmorType;
import me.gravitinos.minigame.bedwars.game.info.PermanentPickaxeType;
import me.gravitinos.minigame.bedwars.game.module.gameitems.ItemSecondChance;
import me.gravitinos.minigame.gamecore.data.Kit;
import me.gravitinos.minigame.gamecore.data.MiniPlayer;
import me.gravitinos.minigame.gamecore.gameitem.GameItemHandler;
import me.gravitinos.minigame.gamecore.module.GameModule;
import me.gravitinos.minigame.gamecore.util.HideUtil;
import me.gravitinos.minigame.gamecore.util.Saving.SavedInventory;
import me.gravitinos.minigame.gamecore.util.Saving.SavedPlayerState;
import me.gravitinos.minigame.gamecore.util.TeamDresser;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ModulePlayerSetup extends GameModule {

    private Map<UUID, SavedInventory> inventoryMap = new HashMap<>();

    public ModulePlayerSetup() {
        super("PLAYER_SETUP");
    }

    /**
     * Procedure for player spawning
     *
     * @param player Player to do procedure to
     * @param spawn  The place to spawn the player
     * @param kit    The player's kit
     * @param team   The player's team
     * @return saved player state
     */
    public SavedPlayerState spawnPlayer(Player player, Location spawn, Kit kit, BedwarsTeam team) {
        SavedPlayerState state = new SavedPlayerState(player);

        boolean hasSecondChance = this.inventoryMap.containsKey(player.getUniqueId());

        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setFallDistance(0);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setVelocity(new Vector(0,0,0));
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.teleport(spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.setNoDamageTicks(20);
        Lists.newArrayList(player.getActivePotionEffects()).forEach(p -> player.removePotionEffect(p.getType()));

        if (!hasSecondChance) {
            player.getInventory().setContents(kit.getContents(MiniPlayer.getPlayer(player.getUniqueId(), player.getName())));
            TeamDresser.dressPlayer(player, team.getColour());

            PermanentArmorType permanentArmorType = ((BedwarsHandler) getGameHandler()).getPlayerInfo(player.getUniqueId()).getPermanentArmorType();

            if (permanentArmorType != PermanentArmorType.LEATHER) {
                player.getInventory().setLeggings(permanentArmorType.getLeggings());
                player.getInventory().setBoots(permanentArmorType.getBoots());
            }

            PermanentPickaxeType permanentPickaxeType = ((BedwarsHandler) getGameHandler()).getPlayerInfo(player.getUniqueId()).getPermanentPickaxeType();

            if (permanentPickaxeType != PermanentPickaxeType.NONE) {
                player.getInventory().addItem(permanentPickaxeType.getItem());
            }
        } else {
            this.inventoryMap.get(player.getUniqueId()).restore(player);
            this.inventoryMap.remove(player.getUniqueId());
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1, true, true));
        }

        HideUtil.unHidePlayer(player);

        return state;
    }

    public SavedPlayerState killPlayer(Player player, boolean elimination) {
        return this.killPlayer(player, elimination, null);
    }

    /**
     * Procedure for player death
     *
     * @param player Player to do procedure to
     * @return saved player state
     */
    public SavedPlayerState killPlayer(Player player, boolean elimination, Player giveItemsTo) {
        SavedPlayerState state = new SavedPlayerState(player);

        BedwarsHandler bedwarsHandler = (BedwarsHandler) getGameHandler();

        boolean hasSecondChance = player.getInventory().containsAtLeast(ItemSecondChance.getItem(), 1);
        if (hasSecondChance && !elimination) {

            //Get rid of one of the items
            ItemStack stack = ItemSecondChance.getItem();

            for (ItemStack contents : player.getInventory().getContents()) {
                if (contents != null && contents.isSimilar(stack)) {
                    if (contents.getAmount() == 1) {
                        player.getInventory().remove(contents);
                    } else {
                        contents.setAmount(contents.getAmount() - 1);
                    }
                }
            }

            //Add to inventory map
            this.inventoryMap.put(player.getUniqueId(), new SavedInventory(player.getInventory().getStorageContents(), player.getInventory().getArmorContents()));
        }

        if (elimination) { //Only drop if it is an elimination
            for (ItemStack contents : player.getInventory().getStorageContents()) {
                if (contents == null) {
                    continue;
                }
                for (GameItemHandler gameItemHandlers : bedwarsHandler.getGameItemsModule().getGameItems()) {
                    if (gameItemHandlers.isMatch(contents) && gameItemHandlers.getName().startsWith("RESOURCE_")) {
                        if (giveItemsTo == null) {
                            player.getWorld().dropItemNaturally(player.getLocation(), contents);
                        } else {
                            giveItemsTo.getInventory().addItem(contents);
                        }
                    }
                }
            }
        }

        if (!elimination) {
            HideUtil.hidePlayer(player);

            BedwarsHandler handler = (BedwarsHandler) this.getGameHandler();

            com.sk89q.worldedit.Vector teleportPoint = handler.getMapRegion().getCenter().setY(handler.getPointTracker().getBed(BedwarsTeam.BLUE).getY() + 20);
            try {
                player.teleport(new Location(Bukkit.getWorld(Objects.requireNonNull(handler.getMapRegion().getWorld()).getName()), teleportPoint.getX(), teleportPoint.getY(), teleportPoint.getZ()), PlayerTeleportEvent.TeleportCause.PLUGIN);
            } catch (Exception e) {
                e.printStackTrace();
            }

            player.getInventory().clear();

            player.setFoodLevel(20);
            player.setSaturation(20);
            player.setVelocity(new Vector(0,0,0));
            player.setGameMode(GameMode.ADVENTURE);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setVelocity(player.getVelocity().add(new Vector(0, 1, 0)));
        }

        if (hasSecondChance && !elimination) {
            player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_THUNDER, 100f, 1f);
            ((BedwarsHandler) getGameHandler()).sendGameMessage(player, "&eYou have a second chance! You will keep your inventory.", "Game");
        }

        return state;
    }

    public SavedPlayerState makeSpectator(Player player) {
        SavedPlayerState state = new SavedPlayerState(player);
        HideUtil.hidePlayer(player);
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getInventory().clear();
        return state;
    }
}
