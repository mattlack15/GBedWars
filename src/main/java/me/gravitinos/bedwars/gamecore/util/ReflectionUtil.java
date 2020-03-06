package me.gravitinos.bedwars.gamecore.util;

import com.google.common.reflect.ClassPath;
import org.bukkit.Bukkit;

import java.util.ArrayList;

public class ReflectionUtil {
    public static ArrayList<Class> getClassesInPackage(String packageName, ClassLoader classLoader) {

        ArrayList<Class> classes = new ArrayList<>();

        try {
            ClassPath classpath = ClassPath.from(classLoader);

            for (ClassPath.ClassInfo classInfo : classpath.getTopLevelClassesRecursive(packageName)) {
                classes.add(classInfo.load());
            }
        }catch(Exception e){
            Bukkit.getLogger().info("Could not get classes in package " + packageName);
        }
        return classes;
    }
}
