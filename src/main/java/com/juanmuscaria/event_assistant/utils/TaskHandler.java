package com.juanmuscaria.event_assistant.utils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskHandler {
    public static final TaskHandler INSTANCE = new TaskHandler();
    private final Queue<Task> tasks = new ConcurrentLinkedQueue<>();

    private TaskHandler() {
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if (!tasks.isEmpty())
            tasks.forEach(task -> {
                task.tick();
                if (task.canRun()) {
                    tasks.remove(task);
                    task.run();
                }
            });
    }

    public void addTask(Task task) {
        tasks.add(task);
    }
}
