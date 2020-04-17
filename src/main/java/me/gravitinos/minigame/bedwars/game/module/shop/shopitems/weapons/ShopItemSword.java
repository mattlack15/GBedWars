package me.gravitinos.minigame.bedwars.game.module.shop.shopitems.weapons;

import me.gravitinos.minigame.bedwars.game.info.BWPlayerInfo;
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

public class ShopItemSword extends ShopItemData {

    private SwordType swordType;

    public ShopItemSword(SwordType swordType){
        this.swordType = swordType;
    }

    public enum SwordType{
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
            p.getInventory().remove(type.getMaterial());
            p.getInventory().addItem(new ItemBuilder(swordType.getMaterial(), 1).setUnbreakable(true).addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE).build());
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
            if(type.equals(this.swordType)){
                return new ItemBuilder(swordType.getMaterial(), 1).setName("&cYou already have this sword!").build();
            }
            if(type.ordinal() > this.swordType.ordinal()){
                return new ItemBuilder(swordType.getMaterial(), 1).setName("&cYou already have a better sword!").build();
            }
            if(ordinal >= SwordType.values().length){
                return new ItemBuilder(swordType.getMaterial(), 1).setName("&c&lMax Level Reached").build();
            }
            return ShopItem.addPrice(new ItemBuilder(swordType.getMaterial(), 1).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).build(), this.getCost(playerInfo, gameItems));
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
        if(type.equals(this.swordType)){
            return new ItemBuilder(Material.BEDROCK, 64).setName("&c&lHopefullyNoOneCopiesThis").build();
        }
        if(type.ordinal() > this.swordType.ordinal()){
            return new ItemBuilder(Material.BEDROCK, 64).setName("&c&lHopefullyNoOneCopiesThis").build();
        }
        if(ordinal >= SwordType.values().length){
            return new ItemBuilder(Material.BEDROCK, 64).setName("&c&lHopefullyNoOneCopiesThis").build();
        }
        switch(swordType){
            case STONE:
                return new ItemBuilder(gameItems.getGameItem(BedwarsItem.RESOURCE_IRON.toString()).getItem(1)).setAmount(10).build();
            case IRON:
                return new ItemBuilder(gameItems.getGameItem(BedwarsItem.RESOURCE_GOLD.toString()).getItem(1)).setAmount(7).build();
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
