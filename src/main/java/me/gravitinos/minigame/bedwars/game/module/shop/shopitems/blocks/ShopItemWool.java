package me.gravitinos.minigame.bedwars.game.module.shop.shopitems.blocks;

import me.gravitinos.minigame.bedwars.game.BedwarsTeam;
import me.gravitinos.minigame.bedwars.game.info.BWPlayerInfo;
import me.gravitinos.minigame.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.minigame.bedwars.game.module.shop.*;
import me.gravitinos.minigame.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.minigame.gamecore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ShopItemWool extends ShopItemData {

    private static final BedwarsItem COST_RESOURCE = BedwarsItem.RESOURCE_IRON;
    private static final int COST_AMOUNT = 3;

    private static final int GIVE_AMOUNT = 16;

    private static final Material GIVE_MATERIAL = Material.WOOL;

    @Override
    public @NotNull Consumer<Player> getGiver(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return (player -> {
            BedwarsTeam team = playerInfo.getTeam();

            switch (team){
                case RED:
                    player.getInventory().addItem(new ItemBuilder(GIVE_MATERIAL, GIVE_AMOUNT, (byte) 14).build());
                    break;
                case GREEN:
                    player.getInventory().addItem(new ItemBuilder(GIVE_MATERIAL, GIVE_AMOUNT, (byte) 5).build());
                    break;
                case YELLOW:
                    player.getInventory().addItem(new ItemBuilder(GIVE_MATERIAL, GIVE_AMOUNT, (byte) 4).build());
                    break;
                case BLUE:
                    player.getInventory().addItem(new ItemBuilder(GIVE_MATERIAL, GIVE_AMOUNT, (byte) 11).build());
                    break;
            }

        });
    }

    @Override
    public @NotNull Supplier<ItemStack> getDisplayItemSupplier(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return () -> {
            BedwarsTeam team = playerInfo.getTeam();
            switch (team){
                case RED:
                   return (new ItemBuilder(GIVE_MATERIAL, GIVE_AMOUNT, (byte) 14).build());
                case GREEN:
                    return (new ItemBuilder(GIVE_MATERIAL, GIVE_AMOUNT, (byte) 5).build());
                case YELLOW:
                    return (new ItemBuilder(GIVE_MATERIAL, GIVE_AMOUNT, (byte) 4).build());
                case BLUE:
                    return (new ItemBuilder(GIVE_MATERIAL, GIVE_AMOUNT, (byte) 11).build());
            }
            return new ItemBuilder(GIVE_MATERIAL, GIVE_AMOUNT).build();
        };
    }

    @Override
    public @NotNull String getSection() {
        return EnumShopSection.SECTION_BLOCKS.getName();
    }

    @Override
    public @NotNull ItemStack getCost(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return new ItemBuilder(gameItems.getGameItem(COST_RESOURCE.toString()).getItem(1)).setAmount(COST_AMOUNT).build();
    }

    @Override
    public boolean isItemCostIncluded() {
        return false;
    }
}
