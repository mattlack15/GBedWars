package me.gravitinos.minigame.bedwars.game.module.shop.shopitems.teamupgrade;

import me.gravitinos.minigame.bedwars.game.info.BWPlayerInfo;
import me.gravitinos.minigame.bedwars.game.info.BWTeamInfo;
import me.gravitinos.minigame.bedwars.game.info.TeamUpgrade;
import me.gravitinos.minigame.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.minigame.bedwars.game.module.shop.EnumShopSection;
import me.gravitinos.minigame.bedwars.game.module.shop.ShopItem;
import me.gravitinos.minigame.bedwars.game.module.shop.ShopItemData;
import me.gravitinos.minigame.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.minigame.gamecore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ShopItemUpgradeProtection extends ShopItemData {

    private static final TeamUpgrade upgrade = TeamUpgrade.PROTECTION;

    @Override
    public @NotNull Consumer<Player> getGiver(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return (p) -> {
            BWTeamInfo teamInfo = playerInfo.getTeamInfo();
            teamInfo.addTeamUpdrade(upgrade, teamInfo.getTeamUpdradeLevel(upgrade)+1);
        };
    }

    @Override
    public @NotNull Supplier<ItemStack> getDisplayItemSupplier(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return () -> {
            BWTeamInfo teamInfo = playerInfo.getTeamInfo();
            int nextLevel = teamInfo.getTeamUpdradeLevel(upgrade);
            if(nextLevel < upgrade.getMaxLevel()){
                nextLevel++;
            } else {
                return new ItemBuilder(Material.DIAMOND_CHESTPLATE, 1).setName("&6&lProtection").addLore("&cMax Level Reached").addItemFlags(ItemFlag.HIDE_ATTRIBUTES).build();
            }
            return ShopItem.addPrice(new ItemBuilder(Material.DIAMOND_CHESTPLATE, 1).setName("&6&lProtection &a" + nextLevel).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).build(), getCost(playerInfo, gameItems));
        };
    }

    @Override
    public @NotNull String getSection() {
        return EnumShopSection.SECTION_TEAM_UPGRADES.getName();
    }

    @Override
    public @NotNull ItemStack getCost(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        BWTeamInfo teamInfo = playerInfo.getTeamInfo();
        int nextLevel = teamInfo.getTeamUpdradeLevel(upgrade);
        if(nextLevel < upgrade.getMaxLevel()){
            nextLevel++;
        } else {
            return new ItemBuilder(Material.BEDROCK, 64).setName("&c&lHopefullyNoOneCopiesThis").build();
        }
        return new ItemBuilder(gameItems.getGameItem(BedwarsItem.RESOURCE_DIAMOND.toString()).getItem(1)).setAmount(nextLevel * 4).build();
    }

    @Override
    public boolean isItemCostIncluded() {
        return true;
    }
}
