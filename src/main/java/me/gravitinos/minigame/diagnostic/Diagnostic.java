package me.gravitinos.minigame.diagnostic;

public abstract class Diagnostic {
    public abstract boolean runDiagnostic();
    public abstract String getMessage();
    public void actions() {}
    public abstract boolean isFixable();
    public abstract boolean fix();
}
