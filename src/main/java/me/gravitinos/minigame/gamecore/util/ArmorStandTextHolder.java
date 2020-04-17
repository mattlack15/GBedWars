package me.gravitinos.minigame.gamecore.util;

import org.bukkit.entity.ArmorStand;

public interface ArmorStandTextHolder {
    void removeStand();
    void createStand(String text);
    ArmorStand getStand();
    void setText(String text);
}
