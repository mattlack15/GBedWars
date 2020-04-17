package me.gravitinos.minigame.bedwars.game.module.shop.shopitems.mining;

import me.gravitinos.minigame.bedwars.game.info.BWPlayerInfo;
import me.gravitinos.minigame.bedwars.game.info.PermanentPickaxeType;
import me.gravitinos.minigame.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.minigame.bedwars.game.module.shop.EnumShopSection;
import me.gravitinos.minigame.bedwars.game.module.shop.ShopItem;
import me.gravitinos.minigame.bedwars.game.module.shop.ShopItemData;
import me.gravitinos.minigame.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.minigame.gamecore.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ShopPermanentItemPickaxe extends ShopItemData {

    @Override
    public @NotNull Consumer<Player> getGiver(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return (p) -> {
            PermanentPickaxeType type = getPickaxeType(p);
            int ordinal = type.ordinal();
            ordinal++;
            if(ordinal >= PermanentPickaxeType.values().length){
                return;
            }
            PermanentPickaxeType nextType = PermanentPickaxeType.values()[ordinal];
            p.getInventory().remove(type.getMaterial());
            p.getInventory().addItem(nextType.getItem());
            playerInfo.setPermanentPickaxeType(nextType);
        };
    }

    public static PermanentPickaxeType getPickaxeType(@NotNull Player p){
        ItemStack[] contents = p.getInventory().getStorageContents();
        for(ItemStack stacks : contents){
            if(stacks == null) continue;
            for(PermanentPickaxeType PickaxeType : PermanentPickaxeType.values()) {
                if (stacks.getType().equals(PickaxeType.getMaterial())){
                    return PickaxeType;
                }
            }
        }
        return PermanentPickaxeType.WOOD;
    }

    @Override
    public @NotNull Supplier<ItemStack> getDisplayItemSupplier(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return () -> {
            Player p = Bukkit.getPlayer(playerInfo.getUuid());
            PermanentPickaxeType type = p != null ? getPickaxeType(p) : PermanentPickaxeType.WOOD;
            int ordinal = type.ordinal();
            ordinal++;
            if(ordinal >= PermanentPickaxeType.values().length){
                return new ItemBuilder(type.getMaterial(), 1).setName("&c&lMax Level Reached").build();
            }
            PermanentPickaxeType nextType = PermanentPickaxeType.values()[ordinal];
            return ShopItem.addPrice(new ItemBuilder(nextType.getMaterial(), 1).setName("&cPermanent " + nextType.getDisplayName() + " Pickaxe").addItemFlags(ItemFlag.HIDE_ATTRIBUTES).build(), this.getCost(playerInfo, gameItems));
        };
    }

    @Override
    public @NotNull String getSection() {
        return EnumShopSection.SECTION_MINING.getName();
    }

    @Override
    public @NotNull ItemStack getCost(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        Player p = Bukkit.getPlayer(playerInfo.getUuid());
        PermanentPickaxeType type = p != null ? getPickaxeType(p) : PermanentPickaxeType.WOOD;
        int ordinal = type.ordinal();
        ordinal++;
        if(ordinal >= PermanentPickaxeType.values().length){
            return new ItemBuilder(Material.BEDROCK, 64).setName("&c&lHopefullyNoOneCopiesThis").build();
        }
        PermanentPickaxeType nextType = PermanentPickaxeType.values()[ordinal];
        switch(nextType){
            case STONE:
                return new ItemBuilder(gameItems.getGameItem(BedwarsItem.RESOURCE_IRON.toString()).getItem(1)).setAmount(12).build();
            case IRON:
                return new ItemBuilder(gameItems.getGameItem(BedwarsItem.RESOURCE_GOLD.toString()).getItem(1)).setAmount(8).build();
            case DIAMOND:
                return new ItemBuilder(gameItems.getGameItem(BedwarsItem.RESOURCE_EMERALD.toString()).getItem(1)).setAmount(4).build();
        }
        return new ItemBuilder(Material.BEDROCK, 64).setName("&c&lHopefullyNoOneCopiesThis").build();
    }

    @Override
    public boolean isItemCostIncluded() {
        return true;
    }
}
