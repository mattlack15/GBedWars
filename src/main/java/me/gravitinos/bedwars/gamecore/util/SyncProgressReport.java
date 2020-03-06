package me.gravitinos.bedwars.gamecore.util;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SyncProgressReport<T> {
    private volatile double percentProgress = 0d;
    private String operationName;
    private ArrayList<Consumer<Double>> listeners = new ArrayList<>();
    private CompletableFuture<T> future = new CompletableFuture<>();

    public SyncProgressReport(String operationName){
        this.operationName = operationName;
    }

    public CompletableFuture<T> getFuture(){
        return this.future;
    }

    public void addListener(Consumer<Double> listener){
        this.listeners.add(listener);
    }

    public synchronized void setPercentProgress(double value){
        this.percentProgress = value;
        this.listeners.forEach(l -> l.accept(value));
    }

    public synchronized double getPercentProgress(){
        if(this.future.isDone()){
            this.setPercentProgress(1);
        }
        return this.percentProgress;
    }

    public String getOperationName() {
        return operationName;
    }
}
