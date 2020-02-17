package me.gravitinos.gamecore.util.Menus;

import me.gravitinos.gamecore.CoreHandler;
import me.gravitinos.gamecore.util.ItemBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class MenuElement {
    private boolean staticItem = true;
    private ItemStack stack;
    private int updateEvery = 2;
    private boolean doUpdates;
    private ClickHandler clickHandler = null;
    private UpdateHandler updateHandler = null;


    public MenuElement(ItemStack stack) {
        this.stack = stack;
    }

    //Getters

    public MenuElement setUpdateHandler(UpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
        return this;
    }

    public MenuElement setClickHandler(ClickHandler clickHandler) {
        this.clickHandler = clickHandler;
        return this;
    }

    public UpdateHandler getUpdateHandler() {
        return updateHandler;
    }

    public ClickHandler getClickHandler() {
        return clickHandler;
    }

    //Util
    public MenuElement setDoUpdates(boolean doUpdates) {
        this.doUpdates = doUpdates;
        return this;
    }

    public MenuElement setUpdateEvery(int updateEvery) {
        this.updateEvery = updateEvery;
        return this;
    }

    /**
     * Adds a temporary lore
     * @param menu The menu this element is assigned to
     * @param lore The lore to add
     * @param ticks The length of time in ticks (20ths of a second) that it will stay in effect
     */
    public void addTempLore(Menu menu, String lore, int ticks){
        int index = menu.indexOfElement(this);
        if(index == -1 || this.getItem() == null){
            return;
        }
        this.setItem(new ItemBuilder(this.getItem()).addLore(lore).build());
        MenuManager.instance.invalidateElementsInInvForMenu(menu, index);
        new BukkitRunnable(){
            @Override
            public void run() {
                if(menu == null){
                    return;
                }
                MenuElement.this.setItem(new ItemBuilder(MenuElement.this.getItem()).removeLore(lore, false).build());
                MenuManager.instance.invalidateElementsInInvForMenu(menu, index);
            }
        }.runTaskLater(CoreHandler.main, ticks);
    }

    public int getUpdateEvery() {
        return updateEvery;
    }

    public boolean isDoingUpdates() {
        return doUpdates;
    }

    public boolean isStaticItem() {
        return staticItem;
    }

    public MenuElement setStaticItem(boolean staticItem) {
        this.staticItem = staticItem;
        return this;
    }

    public MenuElement setItem(ItemStack stack) {
        this.stack = stack;
        return this;
    }

    public ItemStack getItem() {
        return stack;
    }

    //Extra classes and interfaces

    public static interface UpdateHandler {
        public void handleUpdate(MenuElement element);
    }

    public static interface ClickHandler {
        public void handleClick(InventoryClickEvent event, InvInfo info);
    }
}
