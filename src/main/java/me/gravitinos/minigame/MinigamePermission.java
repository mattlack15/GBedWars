package me.gravitinos.minigame;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class MinigamePermission {

    public static final String USE = "game.use";
    public static final String VIEW_DIAGNOSTICS = "game.viewdiagnostics";
    public static final String FORCE_START_GAME = "game.forcestart";
    public static final String FORCE_STOP_GAME = "game.forcestop";

    /**
     * Registers permissions in this class with Spigot
     */
    protected static void registerPermissions(){
        for(Field field : MinigamePermission.class.getDeclaredFields()){
            if(field.getType().equals(String.class) && Modifier.isStatic(field.getModifiers())){
                try {
                    String perm = (String) field.get(null);
                    Permission permission = new Permission(perm);
                    Bukkit.getPluginManager().addPermission(permission);
                } catch (IllegalAccessException ignored) { }
            }
        }
    }
}
