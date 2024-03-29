package me.gravitinos.minigame.gamecore.gameitem;

import me.gravitinos.minigame.gamecore.util.EventSubscriptions;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class GameItemHandler {
    private ModuleGameItems module;
    private String name;
    private boolean enabled = true;

    public GameItemHandler(@NotNull ModuleGameItems module, @NotNull String name){
        this.module = module;
        this.name = name;
        EventSubscriptions.instance.subscribe(this);
    }

    public abstract void onEnable();
    public abstract void onDisable();

    /**
     * Gets the parent module
     * @return The parent module
     */
    public ModuleGameItems getModule(){
        return this.module;
    }

    protected void enable(){
        this.enabled = true;
        EventSubscriptions.instance.subscribe(this);
        this.onEnable();
    }
    protected void disable(){
        this.enabled = false;
        EventSubscriptions.instance.unSubscribe(this);
        this.onDisable();
    }
    public boolean isEnabled(){
        return this.enabled;
    }
    public abstract String getDescription();
    public String getName(){
        return this.name;
    }
    public abstract boolean isMatch(@NotNull ItemStack stack);
    public abstract int getLevel(@NotNull ItemStack stack);
    public abstract ItemStack getItem(int level);

    protected int basicGetLevel(@NotNull ItemStack stack, @NotNull String identifier){
        ItemMeta meta = stack.getItemMeta();
        if(meta == null || !meta.hasLore()){
            return 1;
        }
        List<String> lore = meta.getLore();
        for(String l : lore){
            int index = l.indexOf(identifier);
            if(index != -1){
                index += identifier.length();
                if(l.length() <= index){
                    continue;
                }
                String after = l.substring(index);
                if(after.equals(" ")){
                    continue;
                }
                String num = after.contains(" ") ? after.split(" ")[0] : after;
                try{
                    return Integer.parseInt(num);
                } catch(Exception ignored){}
            }
        }
        return 1;
    }

    protected boolean basicNoLevelIsMatch(@NotNull ItemStack stack){
        ItemStack stack2 = this.getItem(1);
        if(stack == null){
            return stack2 == null;
        }
        ItemStack stack3 = stack.clone();
        stack3.setDurability(stack2.getDurability());
        return stack2.isSimilar(stack3);
    }

    protected boolean basicIsMatch(@NotNull ItemStack stack, String identifier){
        if(stack == null){
            return false;
        }
        ItemMeta meta = stack.getItemMeta();
        if(meta == null || !meta.hasDisplayName()){
            return false;
        }
        return meta.getDisplayName().contains(identifier) || meta.getDisplayName().contains(ChatColor.translateAlternateColorCodes('&', identifier));
    }
}
