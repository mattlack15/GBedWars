package me.gravitinos.bedwars.gamecore.gameitem;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class SimpleGameItemHandler extends GameItemHandler {
    private boolean enabled = false;
    private String itemIdentifier = null;
    private String levelIdentifier = null;


    /**
     * Please use this constructor of your game item incorporates levels/leveling
     * @param module
     * @param name
     * @param itemIdentifier
     * @param levelIdentifier
     */
    public SimpleGameItemHandler(@NotNull ModuleGameItems module, @NotNull String name, @NotNull String itemIdentifier, @NotNull String levelIdentifier){
        super(module, name);
        this.itemIdentifier = itemIdentifier;
        this.levelIdentifier = levelIdentifier;
    }

    /**
     * Please use this constructor if your game item does not incorporate levels/leveling
     * @param module
     * @param name
     */
    public SimpleGameItemHandler(@NotNull ModuleGameItems module, @NotNull String name) {
        super(module, name);
    }

    @Override
    protected void enable() {
        this.enabled = true;
    }

    @Override
    protected void disable() {
        this.enabled = false;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isMatch(@NotNull ItemStack stack) {
        return this.itemIdentifier != null ? this.basicIsMatch(stack, this.itemIdentifier) : this.basicNoLevelIsMatch(stack);
    }

    @Override
    public int getLevel(@NotNull ItemStack stack) {
        return this.levelIdentifier != null ? this.basicGetLevel(stack, this.levelIdentifier) : 1;
    }

    public String getItemIdentifier() {
        return itemIdentifier;
    }

    public void setItemIdentifier(String itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
    }

    public void setLevelIdentifier(String levelIdentifier){
        this.levelIdentifier = levelIdentifier;
    }

    public String getLevelIdentifier() {
        return levelIdentifier;
    }
}
