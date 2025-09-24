package com.jujutsu.event.server;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayDeque;
import java.util.Queue;

public class DelayedTasks {
    private static final Queue<DelayedTask> delayedTasks = new ArrayDeque<>();

    public static void runDelayedTask(MinecraftServer server, int delay, Runnable task) {
        delayedTasks.add(new DelayedTask(server.getTicks(), delay, task));
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for(int i = 0; i < delayedTasks.size(); i++) {
                DelayedTask task = delayedTasks.peek();
                if(task.serverAgeStamp() + task.delay() <= server.getTicks()) {
                    delayedTasks.poll().task().run();
                }
            }
        });
    }

    private record DelayedTask(int serverAgeStamp, int delay, Runnable task) {}
}
