package me.gravitinos.minigame.gamecore.data;

import me.gravitinos.minigame.gamecore.util.EventSubscription;
import me.gravitinos.minigame.gamecore.util.EventSubscriptions;
import org.bukkit.inventory.ItemStack;

/**
 * Kit handler class
 */
public abstract class Kit {

    public Kit(){
        EventSubscriptions.instance.subscribe(this);
    }

    public abstract ItemStack[] getContents(MiniPlayer player);
    public abstract String getName();
}
