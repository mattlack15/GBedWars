package me.gravitinos.bedwars.gamecore.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketListener;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.WeakHashMap;

public class EventSubscriptions implements Listener {

    public static EventSubscriptions instance;

    private WeakList<Object> subscribedObjects = new WeakList<>();
    private WeakHashMap<Object, Class<?>> abstractObjects = new WeakHashMap<>();

    private RegisteredListener registeredListener = new RegisteredListener(EventSubscriptions.this, (listener, event) -> callMethods(event), EventPriority.NORMAL, CoreHandler.main, false);


    public EventSubscriptions() {
        instance = this;

        //Register for all events
        for(Class<?> clazz : ReflectionUtil.getClassesInPackage(Event.class.getPackage().getName(), Event.class.getClassLoader())){
            if(Event.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())){
                try {
                    Method method = clazz.getDeclaredMethod("getHandlerList");
                    HandlerList list = (HandlerList) method.invoke(null);
                    list.register(registeredListener);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException ignored){}
            }
        }

    }

    public synchronized void callMethods(Object e) {

        if(e instanceof PluginDisableEvent){
            if(((PluginDisableEvent) e).getPlugin().equals(CoreHandler.main)){
                this.onDisable();
            }
        }

        //Normal objects
        this.subscribedObjects.removeIf(Objects::isNull);
        for (Object o : this.subscribedObjects) {
            Class<?> c = o.getClass();
            ArrayList<Method> methodsToCheck = Lists.newArrayList(c.getDeclaredMethods());
            for (Method meths : c.getMethods()) {
                if (!methodsToCheck.contains(meths)) {
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
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }

        }

        //Abstract Objects
        for (Object o : this.abstractObjects.keySet()) {
            Class<?> c = this.abstractObjects.get(o);
            ArrayList<Method> methodsToCheck = Lists.newArrayList(c.getDeclaredMethods());
            for (Method meths : c.getMethods()) {
                if (!methodsToCheck.contains(meths)) {
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
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }

        }

    }

    public boolean isSubscribed(Object o){
        return this.subscribedObjects.contains(o);
    }

    public void onDisable() {
        
        for (Object o : this.subscribedObjects) {
            Class<?> c = o.getClass();
            ArrayList<Method> methodsToCheck = Lists.newArrayList(c.getDeclaredMethods());
            for (Method meths : c.getMethods()) {
                if (!methodsToCheck.contains(meths)) {
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

        for (Object o : this.abstractObjects.keySet()) {
            Class<?> c = this.abstractObjects.get(o);
            ArrayList<Method> methodsToCheck = Lists.newArrayList(c.getDeclaredMethods());
            for (Method meths : c.getMethods()) {
                if (!methodsToCheck.contains(meths)) {
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

        for(Player players : Bukkit.getOnlinePlayers()){
            this.unInjectPlayerPacketListener(players);
        }
        
    }

    /**
     * Register normal object
     *
     * @param o Object
     */
    public synchronized void subscribe(Object o) {
        if (!this.subscribedObjects.contains(o)) {
            this.subscribedObjects.add(o);
        }
    }

    /**
     * Register abstract object
     *
     * @param o     Object
     * @param clazz Class of the abstract object
     */
    public synchronized void abstractSubscribe(Object o, Class<?> clazz) {
        if (!this.abstractObjects.containsKey(o)) {
            this.abstractObjects.put(o, clazz);
        }
    }

    public synchronized void unSubscribe(Object o) {
        this.subscribedObjects.remove(o);
    }

    public synchronized void unSubscribeAbstract(Object o) {
        this.abstractObjects.remove(o);
    }



    private void injectPlayerPacketListener(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                Bukkit.getLogger().info("Read " + msg.getClass().getSimpleName());
                if (msg.getClass().getName().contains("Packet")) {
                    PacketEvent<?> event = new PacketEvent<>(msg, player.getUniqueId());
                    callMethods(event);
                    if (event.isCancelled()) {
                        return;
                    }
                }
                super.channelRead(ctx, msg);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                if (msg.getClass().getName().contains("Packet")) {
                    PacketEvent<?> event = new PacketEvent<>(msg, player.getUniqueId());
                    callMethods(event);
                    if (event.isCancelled()) {
                        return;
                    }
                }
                super.write(ctx, msg, promise);
            }
        };
//        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
//        channel.eventLoop().submit(() -> {
//            if (channel.pipeline().get(HANDLER_NAME) == null) {
//                channel.pipeline().addAfter("packet_handler", HANDLER_NAME, channelDuplexHandler);
//            }
//        });

    }


    private void unInjectPlayerPacketListener(Player p) {
//        Channel channel = ((CraftPlayer) p).getHandle().playerConnection.networkManager.channel;
//        channel.eventLoop().submit(() -> {
//            if (channel.pipeline().get(HANDLER_NAME) != null) {
//                channel.pipeline().remove(HANDLER_NAME);
//            }
//        });
    }

//    @EventHandler
//    public void onInteract(PlayerInteractEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerEggThrowEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerBucketEmptyEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerMoveEvent e) { callMethods(e); }
//
//    @EventHandler
//    public void onInteract(PlayerJoinEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerItemConsumeEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerItemDamageEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerQuitEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerTeleportEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerLoginEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(AsyncPlayerPreLoginEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(AsyncPlayerChatEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerVelocityEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerInteractEntityEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerInteractAtEntityEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerDeathEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerChatTabCompleteEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerHandshakeEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerGameModeChangeEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerItemMendEvent e) { callMethods(e); }
//
//    public void onInteract(org.bukkit.event.inventory.InventoryClickEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.inventory.InventoryCloseEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.inventory.InventoryOpenEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerEditBookEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerItemHeldEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerAnimationEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerRespawnEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerItemBreakEvent e) { callMethods(e); }    public void onInteract(PlayerBedEnterEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerDropItemEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerBedLeaveEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerEditBookEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerItemHeldEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerAnimationEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerRespawnEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerItemBreakEvent e) { callMethods(e); }    public void onInteract(PlayerBedEnterEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerDropItemEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerBedLeaveEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerEditBookEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerItemHeldEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerAnimationEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerRespawnEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerItemBreakEvent e) { callMethods(e); }    public void onInteract(PlayerBedEnterEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerDropItemEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerBedLeaveEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerEditBookEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerItemHeldEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerAnimationEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerRespawnEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerItemBreakEvent e) { callMethods(e); }
//    public void onInteract(PlayerBedEnterEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerDropItemEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerBedLeaveEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerEditBookEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerItemHeldEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerAnimationEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerRespawnEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerItemBreakEvent e) { callMethods(e); }    public void onInteract(PlayerBedEnterEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerDropItemEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerBedLeaveEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerEditBookEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerItemHeldEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerAnimationEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerRespawnEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerItemBreakEvent e) { callMethods(e); }    public void onInteract(PlayerBedEnterEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerDropItemEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(PlayerBedLeaveEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerEditBookEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerItemHeldEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerAnimationEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerRespawnEvent e) { callMethods(e); }
//    @EventHandler
//    public void onInteract(org.bukkit.event.player.PlayerItemBreakEvent e) { callMethods(e); }













}
