package me.gravitinos.bedwars.game.module.shop;

import me.gravitinos.bedwars.game.info.BWPlayerInfo;
import me.gravitinos.bedwars.game.module.shop.shopitems.armour.ShopItemPermanentArmour;
import me.gravitinos.bedwars.game.module.shop.shopitems.blocks.*;
import me.gravitinos.bedwars.game.module.shop.shopitems.mining.ShopItemPickaxe;
import me.gravitinos.bedwars.game.module.shop.shopitems.mining.ShopItemShears;
import me.gravitinos.bedwars.game.module.shop.shopitems.misc.*;
import me.gravitinos.bedwars.game.module.shop.shopitems.teamupgrade.ShopItemUpgradeHaste;
import me.gravitinos.bedwars.game.module.shop.shopitems.teamupgrade.ShopItemUpgradeProtection;
import me.gravitinos.bedwars.game.module.shop.shopitems.teamupgrade.ShopItemUpgradeSharpness;
import me.gravitinos.bedwars.game.module.shop.shopitems.weapons.ShopItemArrow;
import me.gravitinos.bedwars.game.module.shop.shopitems.weapons.ShopItemBow;
import me.gravitinos.bedwars.game.module.shop.shopitems.weapons.ShopItemSword;
import me.gravitinos.bedwars.gamecore.gameitem.ModuleGameItems;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public enum EnumShopItem {

    BLOCK_WOOL(new ShopItemWool()),
    BLOCK_SANDSTONE(new ShopItemSandstone()),
    BLOCK_ENDSTONE(new ShopItemEndstone()),
    BLOCK_OBSIDIAN(new ShopItemObsidian()),
    BLOCK_LADDER(new ShopItemLadder()),

    ARMOUR_ADAPTABLE_PERMANENT(new ShopItemPermanentArmour()),

    WEAPON_ADAPTABLE_SWORD(new ShopItemSword()),
    WEAPON_BOW(new ShopItemBow()),
    WEAPON_ARROW(new ShopItemArrow()),

    MISC_ENDERPEARL(new ShopItemEnderpearl()),
    MISC_GOLDEN_APPLE(new ShopItemGoldenApple()),
    MISC_INVIS_POT(new ShopItemInvisPot()),
    MISC_EXPLOSIVE_BOW(new ShopItemExplosiveBow()),
    MISC_TNT(new ShopItemTnt()),
    MISC_KNOCKBACK_STICK(new ShopItemKBStick()),
    MISC_WATER_BUCKET(new ShopItemWaterBucket()),
    MISC_HEALING_POTION(new ShopItemHealthPotion()),
    MISC_BRIDGE_EGG(new ShopItemBridgeEgg()),
    MISC_DEFECTIVE_ENDERPEARL(new ShopItemDefectiveEnderpearl()),

    MINING_ADAPTABLE_PICKAXE(new ShopItemPickaxe()),
    MINING_SHEARS(new ShopItemShears()),

    TEAM_UPGRADE_PROTECTION(new ShopItemUpgradeProtection()),
    TEAM_UPGRADE_SHARPNESS(new ShopItemUpgradeSharpness()),
    TEAM_UPGRADE_HASTE(new ShopItemUpgradeHaste());


    private ShopItemData data;

    EnumShopItem(ShopItemData data){
        this.data = data;
    }

    public String getSection(){
        return data.getSection();
    }
    public ItemStack getCost(BWPlayerInfo info, ModuleGameItems gameItems){
        return data.getCost(info, gameItems);
    }
    public Consumer<Player> getGiver(BWPlayerInfo info, ModuleGameItems gameItems){
        return data.getGiver(info, gameItems);
    }

    public Supplier<ItemStack> getDisplayItemSupplier(BWPlayerInfo info, ModuleGameItems gameItems){
        return data.getDisplayItemSupplier(info, gameItems);
    }

    public boolean isCostIncluded(){
        return data.isItemCostIncluded();
    }
}
