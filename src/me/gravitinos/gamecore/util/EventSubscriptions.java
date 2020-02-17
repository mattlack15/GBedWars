package me.gravitinos.gamecore.util;

import com.google.common.collect.Lists;
import me.gravitinos.gamecore.CoreHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class EventSubscriptions implements Listener {

    public static EventSubscriptions instance;

    private WeakList<Object> subscribedObjects = new WeakList<>();

    public EventSubscriptions() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, CoreHandler.main);
    }

    public void callMethods(Event e) {
        for (Object o : this.subscribedObjects) {
            Class<?> c = o.getClass();
            ArrayList<Method> methodsToCheck = Lists.newArrayList(c.getDeclaredMethods());
            for(Method meths : c.getMethods()){
                if(!methodsToCheck.contains(meths)){
                    methodsToCheck.add(meths);
                }
            }
            for (Method methods : methodsToCheck) {
                if (methods.isAnnotationPresent(EventSubscription.class)) {
                    if (methods.getParameterCount() > 0) {
                        Class<?> inType = methods.getParameterTypes()[0];
                        if (inType.isInstance(e) || inType.isAssignableFrom(e.getClass())) {
                            try {
                                methods.setAccessible(true);
                                methods.invoke(o, e);
                            } catch (IllegalAccessException ex) {
                                ex.printStackTrace();
                            } catch (InvocationTargetException ex) {
                                ex.getTargetException().printStackTrace();
                            }
                        }
                    }
                }
            }

        }
    }

    public void onDisable(){
        for (Object o : this.subscribedObjects) {
            Class<?> c = o.getClass();
            ArrayList<Method> methodsToCheck = Lists.newArrayList(c.getDeclaredMethods());
            for(Method meths : c.getMethods()){
                if(!methodsToCheck.contains(meths)){
                    methodsToCheck.add(meths);
                }
            }
            for (Method methods : methodsToCheck) {
                if (methods.isAnnotationPresent(OnDisable.class)) {
                    if (methods.getParameterCount() == 0) {
                        try {
                            methods.setAccessible(true);
                            methods.invoke(o);
                        } catch (IllegalAccessException ex) {
                            ex.printStackTrace();
                        } catch (InvocationTargetException ex) {
                            ex.getTargetException().printStackTrace();
                        }
                    }
                }
            }

        }
    }

    public synchronized void subscribe(Object o) {
        if (!this.subscribedObjects.contains(o)) {
            this.subscribedObjects.add(o);
        }
    }

    public synchronized void unSubscribe(Object o) {
        this.subscribedObjects.remove(o);
    }


//    @EventHandler
//    public void onEvent(Event event){
//        callMethods(event);
//    }
    @EventHandler
    public void onBreak(BlockBreakEvent e){ callMethods(e); }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) { callMethods(e);}
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) { callMethods(e);}
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {callMethods(e);}
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {callMethods(e);}
    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {callMethods(e);}
    @EventHandler
    public void onPlace(BlockPlaceEvent e){ callMethods(e); }
    @EventHandler
    public void onInteract(PlayerInteractEvent e){ callMethods(e); }
    @EventHandler
    public void onHold(PlayerItemHeldEvent e) { callMethods(e); }
    @EventHandler
    public void onJoin(PlayerJoinEvent e){ callMethods(e); }
    @EventHandler
    public void onQuit(PlayerQuitEvent e){ callMethods(e); }
    @EventHandler
    public void onMove(PlayerMoveEvent e){ callMethods(e); }
    @EventHandler
    public void onDeath(PlayerDeathEvent e){ callMethods(e); }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){ callMethods(e); }
    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e){ callMethods(e); }
    @EventHandler
    public void onDamage(EntityDamageEvent e){ callMethods(e); }
}
