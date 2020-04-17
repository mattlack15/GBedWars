package me.gravitinos.minigame.bedwars.anticheat.check.checks.movement;

import me.gravitinos.minigame.bedwars.anticheat.check.AlertType;
import me.gravitinos.minigame.bedwars.anticheat.check.Check;
import me.gravitinos.minigame.bedwars.anticheat.check.CheckType;
import me.gravitinos.minigame.bedwars.anticheat.check.PunishType;
import me.gravitinos.minigame.bedwars.anticheat.data.Profile;

public class NoSwingA extends Check {
    public NoSwingA(Profile profile, String name, CheckType checkType, AlertType alertType, PunishType punishType) {
        super(profile, name, checkType, alertType, punishType);
    }
}
