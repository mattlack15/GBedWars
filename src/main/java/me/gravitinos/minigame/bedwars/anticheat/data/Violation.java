package me.gravitinos.minigame.bedwars.anticheat.data;

public class Violation {
    private int severity;
    private long time;
    public Violation(int severity){
        this.severity = severity;
        this.time = System.currentTimeMillis();
    }

    public long getTime() {
        return time;
    }

    public int getSeverity() {
        return severity;
    }
}
