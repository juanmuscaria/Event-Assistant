package com.juanmuscaria.event_assistant.utils;

public abstract class Task {
    private int runAfter = 0;
    private int counter = 0;

    protected Task(int runAfter) {
        this.runAfter = runAfter;
    }

    public abstract void run();

    void tick() {
        counter++;
    }

    boolean canRun() {
        return counter >= runAfter;
    }
}
