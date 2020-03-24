package me.gravitinos.bedwars.game.module.shop.shopitems.armour;

import me.gravitinos.bedwars.game.info.BWPlayerInfo;
import me.gravitinos.bedwars.game.info.PermanentArmorType;
import me.gravitinos.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.bedwars.game.module.shop.EnumShopSection;
import me.gravitinos.bedwars.game.module.shop.ShopItem;
import me.gravitinos.bedwars.game.module.shop.ShopItemData;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ShopItemPermanentArmour extends ShopItemData {

    @Override
    public @NotNull Consumer<Player> getGiver(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return (p) -> {
        int ordinal = playerInfo.getPermanentArmorType().ordinal();
        ordinal++;
        if (PermanentArmorType.values().length <= ordinal) {
            return;
        }
        playerInfo.setPermanentArmorType(PermanentArmorType.values()[ordinal]);
        p.getInventory().setLeggings(PermanentArmorType.values()[ordinal].getLeggings());
        p.getInventory().setBoots(PermanentArmorType.values()[ordinal].getBoots());

        };
    }

    @Override
    public @NotNull Supplier<ItemStack> getDisplayItemSupplier(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return () -> {
            int ordinal = playerInfo.getPermanentArmorType().ordinal();
            ordinal++;
            if (PermanentArmorType.values().length <= ordinal) {
                return new ItemBuilder(playerInfo.getPermanentArmorType().getChestplate()).setName("&c&lMax Level Reached").build();
            }
            PermanentArmorType type = PermanentArmorType.values()[ordinal];
            return ShopItem.addPrice(new ItemBuilder(type.getChestplate()).setName("&cPermanent " + type.getDisplayName() + "&c Armour").build(), this.getCost(playerInfo, gameItems));
        };
    }

    @Override
    public @NotNull String getSection() {
        return EnumShopSection.SECTION_ARMOUR.getName();
    }

    @Override
    public @NotNull ItemStack getCost(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        int ordinal = playerInfo.getPermanentArmorType().ordinal();
        ordinal++;
        if (PermanentArmorType.values().length <= ordinal) {
            return new ItemBuilder(Material.BEDROCK, 64).setName("&c&lHopefullyNoOneWillCopyThis").build();
        }
        PermanentArmorType type = PermanentArmorType.values()[ordinal];
        switch(type){
            case IRON:
                return new ItemBuilder(gameItems.getGameItem(BedwarsItem.RESOURCE_GOLD.toString()).getItem(1)).setAmount(6).build();
            case DIAMOND:
                return new ItemBuilder(gameItems.getGameItem(BedwarsItem.RESOURCE_EMERALD.toString()).getItem(1)).setAmount(4).build();
        }
        return new ItemBuilder(Material.BEDROCK, 64).setName("&c&lHopefullyNoOneWillCopyThis").build();
    }

    @Override
    public boolean isItemCostIncluded() {
        return true;
    }
}
