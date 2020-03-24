package me.gravitinos.bedwars.game.module.shop.shopitems.weapons;

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

public class ShopItemSword extends ShopItemData {

    private enum SwordType{
        WOOD("&e&lWood", Material.WOOD_SWORD),
        STONE("&7&lStone", Material.STONE_SWORD),
        IRON("&f&lIron", Material.IRON_SWORD),
        DIAMOND("&b&lDiamond", Material.DIAMOND_SWORD);

        private Material mat;
        private String displayName;
        SwordType(String displayName, Material mat){
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
            SwordType type = getSwordType(p);
            int ordinal = type.ordinal();
            ordinal++;
            if(ordinal >= SwordType.values().length){
                return;
            }
            SwordType nextType = SwordType.values()[ordinal];
            p.getInventory().remove(type.getMaterial());
            p.getInventory().addItem(new ItemBuilder(nextType.getMaterial(), 1).setUnbreakable(true).build());
        };
    }

    public static SwordType getSwordType(@NotNull Player p){
        ItemStack[] contents = p.getInventory().getStorageContents();
        for(ItemStack stacks : contents){
            if(stacks == null) continue;
            for(SwordType swordType : SwordType.values()) {
                if (stacks.getType().equals(swordType.getMaterial())){
                    return swordType;
                }
            }
        }
        return SwordType.WOOD;
    }

    @Override
    public @NotNull Supplier<ItemStack> getDisplayItemSupplier(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        return () -> {
            Player p = Bukkit.getPlayer(playerInfo.getUuid());
            SwordType type = p != null ? getSwordType(p) : SwordType.WOOD;
            int ordinal = type.ordinal();
            ordinal++;
            if(ordinal >= SwordType.values().length){
                return new ItemBuilder(type.getMaterial(), 1).setName("&c&lMax Level Reached").build();
            }
            SwordType nextType = SwordType.values()[ordinal];
            return ShopItem.addPrice(new ItemBuilder(nextType.getMaterial(), 1).build(), this.getCost(playerInfo, gameItems));
        };
    }

    @Override
    public @NotNull String getSection() {
        return EnumShopSection.SECTION_WEAPONS.getName();
    }

    @Override
    public @NotNull ItemStack getCost(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        Player p = Bukkit.getPlayer(playerInfo.getUuid());
        SwordType type = p != null ? getSwordType(p) : SwordType.WOOD;
        int ordinal = type.ordinal();
        ordinal++;
        if(ordinal >= SwordType.values().length){
            return new ItemBuilder(Material.BEDROCK, 64).setName("&c&lHopefullyNoOneCopiesThis").build();
        }
        SwordType nextType = SwordType.values()[ordinal];
        switch(nextType){
            case STONE:
                return new ItemBuilder(gameItems.getGameItem(BedwarsItem.RESOURCE_IRON.toString()).getItem(1)).setAmount(10).build();
            case IRON:
                return new ItemBuilder(gameItems.getGameItem(BedwarsItem.RESOURCE_GOLD.toString()).getItem(1)).setAmount(4).build();
            case DIAMOND:
                return new ItemBuilder(gameItems.getGameItem(BedwarsItem.RESOURCE_EMERALD.toString()).getItem(1)).setAmount(2).build();
        }
        return new ItemBuilder(Material.BEDROCK, 64).setName("&c&lHopefullyNoOneCopiesThis").build();
    }

    @Override
    public boolean isItemCostIncluded() {
        return true;
    }
}
