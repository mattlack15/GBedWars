package me.gravitinos.bedwars.gamecore.util.Menus;

import org.bukkit.inventory.Inventory;

public class InvInfo {
    private Inventory currentInv;
    private Menu currentMenu;
    private Object[] data;

    public InvInfo(Inventory cInv, Menu cMenu){
        this.data = new Object[0];
        this.currentMenu = cMenu;
        this.currentInv = cInv;
    }
    public InvInfo(Inventory cInv, Menu cMenu, Object... data){
        this.currentInv = cInv;
        this.currentMenu = cMenu;
        this.data = data;
    }


    public void setData(Object[] data) {
        this.data = data;
    }

    public Menu getCurrentMenu(){
        return this.currentMenu;
    }

    public void setCurrentMenu(Menu currentMenu) {
        this.currentMenu = currentMenu;
    }

    public Object[] getData() {
        return data;
    }

    public void setCurrentInv(Inventory currentInv) {
        this.currentInv = currentInv;
    }
    public Inventory getCurrentInv(){
        return this.currentInv;
    }


}
