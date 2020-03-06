package me.gravitinos.bedwars.gamecore.util;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
	private ItemStack item;
	public ItemBuilder(Material m, int amount) {
		if(m == null) {
			this.item = null;
			return;
		}
		this.item = new ItemStack(m, amount);
	}
	public ItemBuilder(Material m, int amount, byte data) {
		if(m == null) {
			this.item = null;
			return;
		}
		this.item = new ItemStack(m, amount, data);
	}
	public ArrayList<String> getLore(){
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();
		return Lists.newArrayList(lore);
	}

	public String getName(){
		String name = item.getItemMeta().getDisplayName();
		if(name == null){
			return "";
		}
		return name;
	}

	public ItemBuilder setupAsSkull(String owner){ //TODO add the restoring of name and lore and unbreaking settings to item after reiniting
		ArrayList<String> lore = this.getLore();
		String name = this.getName();
		this.item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		this.setSkullOwner(owner);
		this.setName(name);
		lore.forEach(this::addLore);
		return this;
	}

	public ItemBuilder setUnbreakable(boolean bool){
		ItemMeta meta = this.item.getItemMeta();
		meta.spigot().setUnbreakable(bool);
		this.item.setItemMeta(meta);
		return this;
	}

	public ItemBuilder setSkullOwner(String owner){
		ItemMeta meta = this.item.getItemMeta();
		if(meta instanceof SkullMeta){
			((SkullMeta) meta).setOwner(owner);
			this.item.setItemMeta(meta);
		}
		return this;
	}

	public ItemBuilder(ItemStack item) {
		this.item = item.clone();
	}
	public ItemBuilder setAmount(int amount) {
		item.setAmount(amount);
		return this;
	}
	public ItemBuilder addLore(String s) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();
		lore.add(ChatColor.translateAlternateColorCodes('&', s));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return this;
	}
	public ItemBuilder removeLore(String lore, boolean removeAll) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore1 = meta.getLore() != null ? meta.getLore() : new ArrayList<>();
		while (lore1.contains(ChatColor.translateAlternateColorCodes('&', lore))){
			lore1.remove(ChatColor.translateAlternateColorCodes('&', lore));
			if(!removeAll){
				break;
			}
		}
		meta.setLore(lore1);
		item.setItemMeta(meta);
		return this;
	}
	public ItemBuilder setLore(int i, String s){
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();
		while(lore.size() <= i){
			lore.add("");
		}
		lore.set(i, ChatColor.translateAlternateColorCodes('&', s));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return this;
	}
	public ItemBuilder setName(String s) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', s));
		item.setItemMeta(meta);
		return this;
	}
	public ItemStack build() {
		return this.item;
	}
}
