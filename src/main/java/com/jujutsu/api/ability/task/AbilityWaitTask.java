package com.jujutsu.api.ability.task;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

public class AbilityWaitTask {
    private final int maxWaitTime;
    private final boolean isInfinite;

    private int waitTime = 0;

    public AbilityWaitTask(int maxWaitTime, boolean isInfinite) {
        this.maxWaitTime = maxWaitTime;
        this.isInfinite = isInfinite;
    }

    public InteractionResult tick(Player player) {
        if(!isInfinite && waitTime >= maxWaitTime) {
            onTimeout(player);
            finish(player);
            return InteractionResult.SUCCESS;
        }

        waitTick(player);

        if(isFinished(player)) {
            finish(player);
            return InteractionResult.SUCCESS;
        }

        waitTime++;
        return InteractionResult.PASS;
    }

    public void finish(Player player) {
        onFinished(player);
    }

    protected void waitTick(Player player) { }
    protected void onTimeout(Player player) { }
    protected void onFinished(Player player) { }

    public boolean isFinished(Player player) {
        return false;
    }

    public int waitTime() {
        return waitTime;
    }

    public int maxWaitTime() {
        return maxWaitTime;
    }

    public boolean isInfinite() {
        return isInfinite;
    }
}
