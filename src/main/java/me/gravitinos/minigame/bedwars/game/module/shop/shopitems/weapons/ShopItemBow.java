package me.gravitinos.minigame.bedwars.game.module.shop.shopitems.weapons;

import me.gravitinos.minigame.bedwars.game.info.BWPlayerInfo;
import me.gravitinos.minigame.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.minigame.bedwars.game.module.shop.EnumShopSection;
import me.gravitinos.minigame.bedwars.game.module.shop.ShopItem;
import me.gravitinos.minigame.bedwars.game.module.shop.ShopItemData;
import me.gravitinos.minigame.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.minigame.gamecore.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ShopItemBow extends ShopItemData {

    public static final String BOW_NAME = ChatColor.YELLOW + "Spamming Device";

    @Override
    public @NotNull Consumer<Player> getGiver(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return (p) -> {
            if(p.getInventory().contains(Material.BOW)){
                ItemStack stack = p.getInventory().getItem(p.getInventory().first(Material.BOW));
                if(new ItemBuilder(stack).getName().equals(BOW_NAME)){
                    p.sendMessage("&cYou already have a bow!");
                    return;
                }
            }
            p.getInventory().addItem(new ItemBuilder(Material.BOW, 1).setName(BOW_NAME).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true).build());
        };
    }

    @Override
    public @NotNull Supplier<ItemStack> getDisplayItemSupplier(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return () -> {
            Player p = Bukkit.getPlayer(playerInfo.getUuid());

            if(p == null) return new ItemBuilder(Material.BOW, 1).addLore("&c&lError").build();

            if(p.getInventory().contains(Material.BOW)){
                ItemStack stack = p.getInventory().getItem(p.getInventory().first(Material.BOW));
                if(new ItemBuilder(stack).getName().equals(BOW_NAME)) {
                    return new ItemBuilder(Material.BOW, 1).addLore("&c&lYou already have a bow").build();
                }
            }
            return ShopItem.addPrice(new ItemBuilder(Material.BOW, 1).setName(BOW_NAME).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).build(), this.getCost(playerInfo, gameItems));
        };
    }

    @Override
    public @NotNull String getSection() {
        return EnumShopSection.SECTION_WEAPONS.getName();
    }

    @Override
    public @NotNull ItemStack getCost(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {

        Player p = Bukkit.getPlayer(playerInfo.getUuid());

        if(p == null) return new ItemBuilder(Material.BEDROCK, 64).setName("&c&lHOPDKSFPESF").build();

        if(p.getInventory().contains(Material.BOW)){
            ItemStack stack = p.getInventory().getItem(p.getInventory().first(Material.BOW));
            if(new ItemBuilder(stack).getName().equals(BOW_NAME)) {
                return new ItemBuilder(Material.BEDROCK, 64).setName("&c&lHOPDKSFPESF").build();
            }
        }
        return new ItemBuilder(gameItems.getGameItem(BedwarsItem.RESOURCE_GOLD.toString()).getItem(1)).setAmount(6).build();
    }

    @Override
    public boolean isItemCostIncluded() {
        return true;
    }
}
