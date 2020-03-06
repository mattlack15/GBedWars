package me.gravitinos.bedwars.gamecore.util;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AnnotationUtils<T extends Annotation> {
	public void callAnnotatedDeclaredMethods(@NotNull Class<?> c, Object o, Class<T> a, Object ...args) {
		for(Method m : c.getDeclaredMethods()) {
			Annotation ann = m.getAnnotation(a);
			if(ann != null) {
				try {
					m.setAccessible(true);
					m.invoke(o, args);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignored) {}
			}
		}
	}
	public void callAnnotatedMethods(@NotNull Class<?> c, Object o, Class<T> a, Object ...args) {
		for(Method m : c.getMethods()) {
			Annotation ann = m.getAnnotation(a);
			if(ann != null) {
				try {
					m.invoke(o, args);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignored) {}
			}
		}
	}
}
