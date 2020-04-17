package me.gravitinos.minigame.bedwars.game;

import me.gravitinos.minigame.SpigotMinigames;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Files {
	
	//
	@PluginFolder
	public static final File MAPS_FOLDER = new File(SpigotMinigames.instance.getDataFolder() + File.separator + "maps");


	
	public Files() {
		Class<?> c = this.getClass();
		Field fields[] = c.getDeclaredFields();
		for(Field f : fields) {
			if(Modifier.isStatic(f.getModifiers())){
				if(f.getType() == File.class) {
					if(f.getAnnotation(PluginFile.class) != null) {
						try {
							File file = (File) f.get(null);
							try {
								SpigotMinigames.instance.saveResource(file.getPath(), false);
							} catch(Exception e) {
							
							}
							if(!file.exists()) {
								new File(file.getPath().substring(0, file.getPath().lastIndexOf(File.separator))).mkdirs();
								file.createNewFile();
							}
						} catch (IllegalArgumentException | IllegalAccessException | IOException e) {
							e.printStackTrace();
						}
					} else if(f.getAnnotation(PluginFolder.class) != null) {
						try {
							File file = (File) f.get(null);
							file.mkdirs();
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
					}
					
				}
			}
		}
	}
}
