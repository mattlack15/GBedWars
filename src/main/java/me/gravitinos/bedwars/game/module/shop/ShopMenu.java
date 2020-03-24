package me.gravitinos.bedwars.game.module.shop;

import com.google.common.collect.Lists;
import me.gravitinos.bedwars.gamecore.util.ItemBuilder;
import me.gravitinos.bedwars.gamecore.util.Menus.Menu;
import me.gravitinos.bedwars.gamecore.util.Menus.MenuElement;
import me.gravitinos.bedwars.gamecore.util.Menus.MenuManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ShopMenu extends Menu {
    private Shop shop;
    public ShopMenu(Shop shop){
        super("Shop", 6);
        this.shop = shop;
        this.setup(this.shop.getInventory().getMainSection());
    }

    public ShopMenu(Shop shop, String section){
        super("Shop", 6);
        this.shop = shop;
        this.setup(section);
    }

    private void setup(@NotNull String section){
        this.setAll(null);
        this.fillElement(new MenuElement(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName(" ").build())
        .setClickHandler((e, i) -> ((Player)e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.ENTITY_ITEM_BREAK, 0.8f, 1f)));

        int i = 10;
        for(ShopSection sections : shop.getInventory().getSections()){
            if(i % 9 == 8){
                i = (i - (i%9)) + 9 + 1; //Make i = to next line but at the same col as starting position
            }
            ItemStack stack = sections.getDisplayItem();
            this.setElement(i, new MenuElement(stack).setClickHandler((event, invInfo) -> setup(sections.getSectionName())));
            i++;
        }

        int START_POS = 29;
        int pos = START_POS;

        for(ShopItem items : shop.getInventory().getItems(section)){
            if(pos % 9 == 7){
                pos = (pos - (pos%9)) + 9 + START_POS % 9; //Make pos = to next line but at the same col as START_POS
            }

            this.setElement(pos, new MenuElement(items.getDisplayItem()).setClickHandler(((event, info) -> {
                Player p = ((Player)event.getWhoClicked());

                if (event.getClick().equals(ClickType.DOUBLE_CLICK)) {
                    return;
                }

                //Check and remove needed
                if(items.getNeeded() != null) {
                    if (!p.getInventory().containsAtLeast(items.getNeeded().get(), items.getNeeded().get().getAmount())) {
                        p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 2f, 1f);
                        this.getElement(event.getSlot()).addTempLore(this, "&cYou don't have enough materials to buy this!", 60);
                        return;
                    }
                    int amount = items.getNeeded().get().getAmount();
                    for (ItemStack stacks : Lists.newArrayList(p.getInventory().getContents())) {
                        if(stacks == null){
                            continue;
                        }
                        if (stacks.isSimilar(items.getNeeded().get())) {
                            if (amount <= 0) {
                                break;
                            }
                            amount -= stacks.getAmount();
                            if (amount >= 0) {
                                p.getInventory().remove(stacks);
                            } else {
                                stacks.setAmount(stacks.getAmount() - (amount + stacks.getAmount()));
                            }
                        }
                    }
                }

                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2f, 1f);
                items.getGiver().accept(p);
                setup(section);
            })));

            pos++;
        }

        MenuManager.instance.invalidateInvsForMenu(this);
    }
}
