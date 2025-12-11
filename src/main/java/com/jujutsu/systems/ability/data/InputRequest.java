package com.jujutsu.systems.ability.data;

import com.jujutsu.systems.ability.task.AbilityTask;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

public class InputRequest {
    public final RequestedInputKey key;
    public final AbilityTask task;
    @Nullable
    public final AbilityTask timeoutTask;
    public final int timeout;
    public final boolean showOnScreen;

    public int timeoutTime = 0;

    public InputRequest(RequestedInputKey key, AbilityTask task, @Nullable AbilityTask timeoutTask, int timeout, boolean showOnScreen) {
        this.key = key;
        this.task = task;
        this.timeoutTask = timeoutTask;
        this.timeout = timeout;
        this.showOnScreen = showOnScreen;
    }

    public void executeTimeoutTask(PlayerEntity player) {
        if(this.timeoutTask != null) {
            timeoutTask.execute(player);
        }
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof InputRequest request && key.equals(request.key);
    }
}