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

    private InputRequest(RequestedInputKey key, AbilityTask task, @Nullable AbilityTask timeoutTask, int timeout, boolean showOnScreen) {
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

    public static Builder mouseRequest(int mouseButton, AbilityTask mainTask) {
        Builder builder = new Builder();
        builder.keyCode = -1;
        builder.mouseButton = mouseButton;
        builder.mainTask = mainTask;

        return builder;
    }

    public static Builder keyboardRequest(int keyCode, AbilityTask mainTask) {
        Builder builder = new Builder();
        builder.keyCode = keyCode;
        builder.mouseButton = -1;
        builder.mainTask = mainTask;

        return builder;
    }

    public static class Builder {
        private int keyCode;
        private int mouseButton;
        private int timeout = -1;
        private AbilityTask mainTask;
        private AbilityTask timeoutTask = null;

        private Builder() {}

        public Builder addTimeoutTask(AbilityTask timeoutTask) {
            this.timeoutTask = timeoutTask;
            return this;
        }

        public Builder addTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public InputRequest build() {
            return new InputRequest(
                    new RequestedInputKey(keyCode, mouseButton),
                    mainTask, timeoutTask,
                    timeout,
                    true
            );
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