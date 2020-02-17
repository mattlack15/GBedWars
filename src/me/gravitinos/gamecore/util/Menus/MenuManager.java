package me.gravitinos.gamecore.util.Menus;

import me.gravitinos.gamecore.CoreHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuManager implements Listener {
    public static MenuManager instance;
    private WeakListMenu<Menu> menus = new WeakListMenu<>();
    private Map<UUID, InvInfo> infos = new HashMap<>();
    private BukkitRunnable updateTask = null;

    static {
        new MenuManager();
    }

    public MenuManager() {
        instance = this;

        this.updateTask = new BukkitRunnable() {
            long counter = 0;

            @Override
            public void run() {

                for (Menu m : menus) {
                    Map<Integer, MenuElement> elements = m.getElements();
                    for (int i = 0; i < elements.size(); i++) {
                        MenuElement e = elements.get(i);
                        if (e == null) {
                            return;
                        }
                        if (!e.isDoingUpdates() || e.getUpdateHandler() == null) {
                            continue;
                        }
                        if (counter % e.getUpdateEvery() == 0) {
                            e.getUpdateHandler().handleUpdate(e);
                            MenuManager.instance.invalidateElementsInInvForMenu(m, i);
                        }
                    }
                }
                counter++;
            }
        };
        this.updateTask.runTaskTimer(CoreHandler.main, 0, 2);

        Bukkit.getPluginManager().registerEvents(this, CoreHandler.main);

    }

    public synchronized void addMenu(Menu menu) {
        if (this.menus.contains(menu)) {
            return;
        }
        this.menus.add(menu);
    }

    public InvInfo getInfo(UUID player) {
        if (this.infos.containsKey(player)) {
            return this.infos.get(player);
        }
        return new InvInfo(null, null);
    }

    public void setInfo(UUID player, InvInfo info) {
        this.infos.put(player, info);
    }

    public void invalidateInvsForMenu(Menu m) {
        Menu.doInMainThread(() -> {
            if (m == null) {
                return;
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                InvInfo info = this.getInfo(p.getUniqueId());
                if (info != null) {
                    if (info.getCurrentMenu() != null && info.getCurrentMenu().equals(m)) {
                        m.open(p, info.getData());
                    }
                }
            }
        });
    }

    public void invalidateElementsInInvForMenu(Menu m, int slot) {
        Menu.doInMainThread(() -> {
            if (m == null) {
                return;
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                InvInfo info = this.getInfo(p.getUniqueId());
                if (info != null) {
                    if (info.getCurrentMenu() != null && info.getCurrentMenu().equals(m)) {
                        Inventory inv = info.getCurrentInv();
                        if (slot > -1 && slot < inv.getSize()) { //If the slot is a valid slot in the inventory
                            inv.setItem(slot, m.getElement(slot) != null ? m.getElement(slot).getItem() : null);
                        }
                    }
                }
            }
        });
    }

    private void callClose(UUID player, Inventory inv) {
        if (infos.containsKey(player)) {
            if (infos.get(player).getCurrentMenu() == null) {
                return;
            }
            if (infos.get(player).getCurrentInv() != null && !infos.get(player).getCurrentInv().equals(inv)) {
                return;
            }
            infos.put(player, new InvInfo(null, null));
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        this.callClose(e.getPlayer().getUniqueId(), e.getInventory());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (this.infos.containsKey(e.getWhoClicked().getUniqueId())) {
            InvInfo info = this.getInfo(e.getWhoClicked().getUniqueId());
            if (info.getCurrentInv() != null && info.getCurrentInv().equals(e.getClickedInventory()) && info.getCurrentMenu() != null) {
                if (info.getCurrentMenu().getElement(e.getSlot()) != null) {
                    if (info.getCurrentMenu().getElement(e.getSlot()).isStaticItem()) {
                        e.setCancelled(true);
                    }
                    MenuElement.ClickHandler handler = info.getCurrentMenu().getElement(e.getSlot()).getClickHandler();
                    if (handler != null) {
                        handler.handleClick(e, info);
                    }
                } else {
                    MenuElement.ClickHandler clickHandler = info.getCurrentMenu().getDefaultClickHandler();
                    if (clickHandler != null) {
                        clickHandler.handleClick(e, info);
                    }
                }
            }
        }
    }
}
