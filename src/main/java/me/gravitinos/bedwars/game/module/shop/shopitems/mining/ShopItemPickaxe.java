package me.gravitinos.bedwars.game.module.shop.shopitems.mining;

import me.gravitinos.bedwars.game.info.BWPlayerInfo;
import me.gravitinos.bedwars.game.module.gameitems.BedwarsItem;
import me.gravitinos.bedwars.game.module.shop.EnumShopSection;
import me.gravitinos.bedwars.game.module.shop.ShopItem;
import me.gravitinos.bedwars.game.module.shop.ShopItemData;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ShopItemPickaxe extends ShopItemData {

    private enum PickaxeType{
        WOOD("&e&lWood", Material.WOOD_PICKAXE),
        STONE("&7&lStone", Material.STONE_PICKAXE),
        IRON("&f&lIron", Material.IRON_PICKAXE),
        DIAMOND("&b&lDiamond", Material.DIAMOND_PICKAXE);

        private Material mat;
        private String displayName;
        PickaxeType(String displayName, Material mat){
            this.mat = mat;
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Material getMaterial() {
            return mat;
        }
    }

    @Override
    public @NotNull Consumer<Player> getGiver(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return (p) -> {
            ShopItemPickaxe.PickaxeType type = getPickaxeType(p);
            int ordinal = type.ordinal();
            ordinal++;
            if(ordinal >= ShopItemPickaxe.PickaxeType.values().length){
                return;
            }
            ShopItemPickaxe.PickaxeType nextType = ShopItemPickaxe.PickaxeType.values()[ordinal];
            p.getInventory().remove(type.getMaterial());
            p.getInventory().addItem(new ItemBuilder(nextType.getMaterial(), 1).setUnbreakable(true).build());
        };
    }

    public static ShopItemPickaxe.PickaxeType getPickaxeType(@NotNull Player p){
        ItemStack[] contents = p.getInventory().getStorageContents();
        for(ItemStack stacks : contents){
            if(stacks == null) continue;
            for(ShopItemPickaxe.PickaxeType PickaxeType : ShopItemPickaxe.PickaxeType.values()) {
                if (stacks.getType().equals(PickaxeType.getMaterial())){
                    return PickaxeType;
                }
            }
        }
        return ShopItemPickaxe.PickaxeType.WOOD;
    }

    @Override
    public @NotNull Supplier<ItemStack> getDisplayItemSupplier(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return () -> {
            Player p = Bukkit.getPlayer(playerInfo.getUuid());
            ShopItemPickaxe.PickaxeType type = p != null ? getPickaxeType(p) : ShopItemPickaxe.PickaxeType.WOOD;
            int ordinal = type.ordinal();
            ordinal++;
            if(ordinal >= ShopItemPickaxe.PickaxeType.values().length){
                return new ItemBuilder(type.getMaterial(), 1).setName("&c&lMax Level Reached").build();
            }
            ShopItemPickaxe.PickaxeType nextType = ShopItemPickaxe.PickaxeType.values()[ordinal];
            return ShopItem.addPrice(new ItemBuilder(nextType.getMaterial(), 1).build(), this.getCost(playerInfo, gameItems));
        };
    }

    @Override
    public @NotNull String getSection() {
        return EnumShopSection.SECTION_MINING.getName();
    }

    @Override
    public @NotNull ItemStack getCost(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        Player p = Bukkit.getPlayer(playerInfo.getUuid());
        ShopItemPickaxe.PickaxeType type = p != null ? getPickaxeType(p) : ShopItemPickaxe.PickaxeType.WOOD;
        int ordinal = type.ordinal();
        ordinal++;
        if(ordinal >= ShopItemPickaxe.PickaxeType.values().length){
            return new ItemBuilder(Material.BEDROCK, 64).setName("&c&lHopefullyNoOneCopiesThis").build();
        }
        ShopItemPickaxe.PickaxeType nextType = ShopItemPickaxe.PickaxeType.values()[ordinal];
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
