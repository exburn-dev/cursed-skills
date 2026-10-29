package com.jujutsu.api.ability.task;

import net.minecraft.world.entity.player.Player;

public class InputWaitTask extends AbilityWaitTask {
    private final AbilityTask onPressed;
    private final AbilityTask onTimeout;

    private boolean isPressed = false;

    public InputWaitTask(int maxWaitTime, AbilityTask onPressed, AbilityTask onTimeout) {
        super(maxWaitTime, false);
        this.onPressed = onPressed;
        this.onTimeout = onTimeout;
    }

    public InputWaitTask(AbilityTask onPressed, AbilityTask onTimeout) {
        super(0, true);
        this.onPressed = onPressed;
        this.onTimeout = onTimeout;
    }

    public void onInputPacketReceived(Player player) {
        isPressed = true;
        onPressed.execute(player);
    }

    @Override
    protected void onTimeout(Player player) {
        if(onTimeout != null) {
            onTimeout.execute(player);
        }
    }

    @Override
    public boolean isFinished(Player player) {
        return isPressed;
    }
}
