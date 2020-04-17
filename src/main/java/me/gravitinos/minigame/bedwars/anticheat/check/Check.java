package me.gravitinos.minigame.bedwars.anticheat.check;

import me.gravitinos.minigame.bedwars.anticheat.SpigotAC;
import me.gravitinos.minigame.bedwars.anticheat.check.checks.PackageAnchor;
import me.gravitinos.minigame.bedwars.anticheat.data.Profile;
import me.gravitinos.minigame.bedwars.anticheat.data.Violation;
import me.gravitinos.minigame.bedwars.game.SpigotBedwars;
import me.gravitinos.minigame.gamecore.util.EventSubscriptions;
import me.gravitinos.minigame.gamecore.util.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.UUID;

public abstract class Check {
    private static ArrayList<Profile> profiles = new ArrayList<>();

    private AlertType alertType;
    private CheckType checkType;
    private PunishType punishType;
    private String name;
    private boolean enabled = true;
    private Profile profile;
    private ArrayList<Violation> violations = new ArrayList<>();

    public Check(Profile profile, String name, CheckType checkType, AlertType alertType, PunishType punishType) {
        this.name = name;
        this.checkType = checkType;
        this.alertType = alertType;
        this.punishType = punishType;
        this.profile = profile;
        EventSubscriptions.instance.subscribe(this);
    }

    public static void removeProfile(UUID uniqueId) {
        profiles.forEach(p -> {
            if(p.getUniqueId().equals(uniqueId)){
                p.getChecks().forEach(Check::disable);
            }
        });
        profiles.removeIf(p -> p.getUniqueId().equals(uniqueId));
    }
    public static void clearProfiles(){
        profiles.clear();
    }

    public Profile getProfile() {
        return profile;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void enable() {
        EventSubscriptions.instance.subscribe(this);
        this.enabled = true;
    }

    public void disable() {
        EventSubscriptions.instance.unSubscribe(this);
        this.enabled = false;
    }

    public static synchronized Profile getProfile(UUID userId) {
        for (Profile profile : profiles) {
            if (profile.getUniqueId().equals(userId)) {
                return profile;
            }
        }
        return null;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public CheckType getCheckType() {
        return checkType;
    }

    public PunishType getPunishType() {
        return punishType;
    }

    protected void punish() {
        if (this.punishType.equals(PunishType.EXPERIMENTAL)) {
            if (SpigotBedwars.bedwarsHandler != null && SpigotBedwars.bedwarsHandler.isRunning()) {
                if (SpigotBedwars.bedwarsHandler.isPlaying(profile.getUniqueId()) && !SpigotBedwars.bedwarsHandler.isRespawning(profile.getUniqueId())) {
                    SpigotBedwars.bedwarsHandler.sendServerMessage("&e" + profile.getName() + "&c was kicked for &f" + getName(), "Game");
                    SpigotBedwars.bedwarsHandler.kickPlayer(profile.getUniqueId());
                }
            }
        }
    }

    protected boolean isAlertTime(){
        this.clearViolationsPast(4000);
        return this.violations.size() >= 2;
    }

    public ArrayList<Violation> getViolations(){
        return this.violations;
    }

    protected void addViolation(Violation violation) {
        this.violations.add(violation);
        if(this.isAlertTime()){
            this.violations.clear();
            this.punish();
        }
    }

    protected void clearViolationsPast(long millis) {
        this.violations.removeIf(v -> System.currentTimeMillis() - v.getTime() > millis);
    }

    protected void alert(int severity) {
        //TODO change this further on
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(ChatColor.BLUE + "Game > " + ChatColor.YELLOW + this.profile.getName() + ChatColor.RED + " failed " + ChatColor.WHITE + this.getName() + ChatColor.DARK_RED + " sv. " + severity);
        }
    }


    public static synchronized void createProfile(Player p) {
        Profile profile = new Profile(p);
        profiles.add(profile);

        for (Class<?> clazz : ReflectionUtil.getClassesInPackage(PackageAnchor.class.getPackage().getName(), Check.class.getClassLoader())) {
            if (Check.class.isAssignableFrom(clazz)) {
                try {
                    Constructor constructor = clazz.getDeclaredConstructor(Profile.class);
                    profile.addCheck((Check) constructor.newInstance(profile));
                } catch (NoSuchMethodException e) {
                    SpigotAC.consoleLog("Check \"" + clazz.getSimpleName() + "\" must have a constructor with 1 argument of type Profile");
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    SpigotAC.consoleLog("Problem loading check \"" + clazz.getSimpleName() + "\"");
                    e.printStackTrace();
                }
            }
        }

    }
}
