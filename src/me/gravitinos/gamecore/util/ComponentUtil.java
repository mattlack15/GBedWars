package me.gravitinos.gamecore.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ComponentUtil {
	@NotNull
	public static TextComponent getClickHoverComponent(String text, @NotNull String hover, ClickEvent.Action ac, String click) {
		TextComponent component = new TextComponent(TextComponent.fromLegacyText(text));
		String[] description = hover.split("<nl>");
		TextComponent newLine = new TextComponent(ComponentSerializer.parse("{text: \"\n\"}"));
		TextComponent hover1 = new TextComponent(ComponentUtil.toColor(description[0]));
		for(int i = 1; i < description.length; i++) {
			hover1.addExtra(newLine);
			hover1.addExtra(new TextComponent(TextComponent.fromLegacyText(ComponentUtil.toColor(description[i]))));
		}
		ArrayList<TextComponent> components = new ArrayList<>();
		components.add(hover1);
		component.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, (BaseComponent[])components.toArray(new BaseComponent[components.size()])));
		component.setClickEvent(new ClickEvent(ac, click));
		return component;
	}

	/**
	 * Create a TextComponent from a String
	 * @param text String
	 * @return Text Component
	 */
	public static TextComponent toComponent(String text) {
		return new TextComponent(TextComponent.fromLegacyText(text));
	}

	/**
	 * Convert color codes in the string
	 * @param s String
	 * @return Converted string
	 */
	public static String toColor(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
}
