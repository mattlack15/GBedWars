package me.gravitinos.minigame.bedwars.game.module.shop;

import me.gravitinos.minigame.bedwars.game.info.BWPlayerInfo;
import me.gravitinos.minigame.gamecore.gameitem.ModuleGameItems;

public class PlayerDependantShopInventory extends ShopInventory {
    public PlayerDependantShopInventory(BWPlayerInfo playerInfo, ModuleGameItems gameItems) {
        super(EnumShopSection.SECTION_BLOCKS.getName());

        for(EnumShopSection sections : EnumShopSection.values()){
            this.setSectionDisplayItem(sections.getName(), sections.getDisplayItem());
        }

        for(EnumShopItem item : EnumShopItem.values()){
            this.addShopItem(item.getSection(), new ShopItem(() -> item.getCost(playerInfo, gameItems), item.getDisplayItemSupplier(playerInfo, gameItems),
                    item.getGiver(playerInfo, gameItems), !item.isCostIncluded()));
        }
    }

}
