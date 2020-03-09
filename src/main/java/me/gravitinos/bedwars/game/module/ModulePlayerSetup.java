package me.gravitinos.bedwars.game.module;

import me.gravitinos.bedwars.game.BedwarsHandler;
import me.gravitinos.bedwars.game.BedwarsTeam;
import me.gravitinos.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.bedwars.game.module.playersetup.Kit;
import me.gravitinos.bedwars.gamecore.gameitem.GameItemHandler;
import me.gravitinos.bedwars.gamecore.handler.GameHandler;
import me.gravitinos.bedwars.gamecore.module.GameModule;
import me.gravitinos.bedwars.gamecore.util.HideUtil;
import me.gravitinos.bedwars.gamecore.util.Saving.SavedPlayerState;
import me.gravitinos.bedwars.gamecore.util.TeamDresser;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ModulePlayerSetup extends GameModule {
    public ModulePlayerSetup(@NotNull GameHandler gameHandler) {
        super(gameHandler, "PLAYER_SETUP");
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
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setFallDistance(0);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.teleport(spawn, PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
        player.getEnderChest().clear();

        player.getInventory().setContents(kit.getContents());

        TeamDresser.dressPlayer(player, team.getColour());
        HideUtil.unHidePlayer(player);

        return state;
    }

    /**
     * Procedure for player death
     *
     * @param player Player to do procedure to
     * @return saved player state
     */
    public SavedPlayerState killPlayer(Player player, boolean elimination) {
        SavedPlayerState state = new SavedPlayerState(player);

        BedwarsHandler bedwarsHandler = (BedwarsHandler)getGameHandler();
        for(ItemStack contents : player.getInventory().getStorageContents()) {
            if(contents == null){
                continue;
            }
            for (GameItemHandler gameItemHandlers : bedwarsHandler.getGameItemsModule().getGameItems()) {
                if(gameItemHandlers.isMatch(contents) && gameItemHandlers.getName().startsWith("RESOURCE_")){
                    player.getWorld().dropItemNaturally(player.getLocation(), contents);
                }
            }
        }

        if (!elimination) {
            HideUtil.hidePlayer(player);
            
            BedwarsHandler handler = (BedwarsHandler) this.getGameHandler();
            
            com.sk89q.worldedit.Vector teleportPoint = handler.getMapRegion().getCenter().setY(handler.getPointTracker().getBedBLUE().getY() + 20);
            try {
                player.teleport(new Location(Bukkit.getWorld(Objects.requireNonNull(handler.getMapRegion().getWorld()).getName()), teleportPoint.getX(), teleportPoint.getY(), teleportPoint.getZ()), PlayerTeleportEvent.TeleportCause.COMMAND);
            } catch (Exception e){
                e.printStackTrace();
            }

            player.setFoodLevel(20);
            player.getInventory().clear();
            player.setSaturation(20);
            player.setGameMode(GameMode.ADVENTURE);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setVelocity(player.getVelocity().add(new Vector(0, 1, 0)));
        }
        
        return state;
    }

    public SavedPlayerState makeSpectator(Player player) {
        SavedPlayerState state = new SavedPlayerState(player);
        HideUtil.hidePlayer(player);
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        return state;
    }
}
