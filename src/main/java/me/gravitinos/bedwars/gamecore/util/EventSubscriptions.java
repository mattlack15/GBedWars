package me.gravitinos.bedwars.gamecore.util;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;
import java.util.WeakHashMap;

public class EventSubscriptions extends TinyProtocol implements Listener {

    public static EventSubscriptions instance;
    
    public static final String HANDLER_NAME = "ES_PL_" + UUID.randomUUID().toString();

    private WeakList<Object> subscribedObjects = new WeakList<>();
    private WeakHashMap<Object, Class<?>> abstractObjects = new WeakHashMap<>();

    public EventSubscriptions() {
        super(CoreHandler.main);
        instance = this;

        //Register for all events

        RegisteredListener registeredListener = new RegisteredListener(this, (listener, event) -> onEvent(event), EventPriority.NORMAL, CoreHandler.main, false);
        for (HandlerList handler : HandlerList.getHandlerLists())
            handler.register(registeredListener);

    }

    public void callMethods(Object e) {

        //Normal objects
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

                        if (e instanceof PacketEvent) {
                            Type[] paramTypes = methods.getGenericParameterTypes();
                            if (paramTypes.length < 1 || !(paramTypes[0] instanceof ParameterizedType)) {
                                continue;
                            }
                            Type[] typeArgs = ((ParameterizedType) paramTypes[0]).getActualTypeArguments();
                            if (typeArgs.length < 1) {
                                continue;
                            }
                            if (!((Class<?>) typeArgs[0]).isAssignableFrom(((PacketEvent) e).getPacket().getClass())) {
                                continue;
                            }
                        }
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

    @Override
    public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
        PacketEvent<?> event = new PacketEvent<>(packet, sender != null ? sender.getUniqueId() : null);
        callMethods(event);
        if(event.isCancelled()){
            return null;
        }
        return packet;
    }

    @Override
    public Object onPacketOutAsync(Player sender, Channel channel, Object packet) {
        PacketEvent<?> event = new PacketEvent<>(packet, sender != null ? sender.getUniqueId() : null);
        callMethods(event);
        if(event.isCancelled()){
            return null;
        }
        return packet;
    }

    private void unInjectPlayerPacketListener(Player p) {
//        Channel channel = ((CraftPlayer) p).getHandle().playerConnection.networkManager.channel;
//        channel.eventLoop().submit(() -> {
//            if (channel.pipeline().get(HANDLER_NAME) != null) {
//                channel.pipeline().remove(HANDLER_NAME);
//            }
//        });
    }

    @EventHandler
    public void onEvent(Event e) {
        callMethods(e);
    }
}
