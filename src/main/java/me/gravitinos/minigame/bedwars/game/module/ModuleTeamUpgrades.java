package me.gravitinos.minigame.bedwars.game.module;

import me.gravitinos.minigame.bedwars.game.BedwarsHandler;
import me.gravitinos.minigame.bedwars.game.BedwarsTeam;
import me.gravitinos.minigame.bedwars.game.info.BWTeamInfo;
import me.gravitinos.minigame.bedwars.game.info.TeamUpgrade;
import me.gravitinos.minigame.gamecore.CoreHandler;
import me.gravitinos.minigame.gamecore.module.GameModule;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModuleTeamUpgrades extends GameModule {

    //Also handles invisibility
    private Map<UUID, Boolean> hidden = new HashMap<>();

    private BedwarsHandler gameHandler;
    public ModuleTeamUpgrades(BedwarsHandler gameHandler) {
        super("TEAM_UPGRADES");

        this.gameHandler = gameHandler;
    }

    @Override
    public void enable() {
        super.enable();
        currentTaskId = getTask().runTaskTimer(CoreHandler.main, 0, 5).getTaskId();
    }

    int currentTaskId = -1;

    @Override
    public void disable(){
        super.disable();
        if(currentTaskId != -1)
            Bukkit.getScheduler().cancelTask(currentTaskId);
    }

    private BukkitRunnable getTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {

                for (UUID players : gameHandler.getTeamManagerModule().getPlayers()) {
                    Player p = Bukkit.getPlayer(players);
                    if (p == null) continue;

//                    if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
//                        if (!hidden.containsKey(p.getUniqueId())) {
//                            hidden.put(p.getUniqueId(), HideUtil.isHidden(p.getUniqueId()));
//                            HideUtil.hidePlayer(p);
//                        }
//                    } else if (hidden.containsKey(p.getUniqueId())) {
//                        boolean bool = hidden.get(p.getUniqueId());
//                        if (!bool) {
//                            HideUtil.unHidePlayer(p);
//                        }
//                        hidden.remove(p.getUniqueId());
//                    }
                }

                for (BedwarsTeam team : BedwarsTeam.values()) {
                    BWTeamInfo info = gameHandler.getTeamInfo(team);

                    gameHandler.getTeamManagerModule().getPlayersOnTeam(team.toString()).forEach(tm -> {
                        Player p = Bukkit.getPlayer(tm);
                        if (info.getTeamUpdradeLevel(TeamUpgrade.PROTECTION) > 0) {
                            int level = info.getTeamUpdradeLevel(TeamUpgrade.PROTECTION);
                            if (p != null) {
                                ItemStack[] armorContents = p.getInventory().getArmorContents();

                                for (ItemStack stack : armorContents) {
                                    if (stack == null) continue;
                                    stack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level);
                                }
                                p.getInventory().setArmorContents(armorContents);
                            }
                        }


                        if (info.getTeamUpdradeLevel(TeamUpgrade.SHARPNESS) > 0) {
                            int level = info.getTeamUpdradeLevel(TeamUpgrade.SHARPNESS);
                            if (p != null) {
                                ItemStack[] contents = p.getInventory().getStorageContents();

                                for (ItemStack stack : contents) {
                                    if (stack == null) continue;
                                    if (!stack.containsEnchantment(Enchantment.DAMAGE_ALL) || stack.getEnchantmentLevel(Enchantment.DAMAGE_ALL) != level) {
                                        if (Enchantment.DAMAGE_ALL.canEnchantItem(stack)) {
                                            stack = p.getInventory().getItem(p.getInventory().first(stack));
                                            stack.addEnchantment(Enchantment.DAMAGE_ALL, level);
                                        }
                                    }
                                }
                            }
                        }

                        if (info.getTeamUpdradeLevel(TeamUpgrade.HASTE) > 0) {
                            int level = info.getTeamUpdradeLevel(TeamUpgrade.SHARPNESS);
                            if (p != null) {
                                p.removePotionEffect(PotionEffectType.FAST_DIGGING);
                                p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 40, level, true, false));
                            }
                        }
                    });
                }
            }
        };
    }


}
