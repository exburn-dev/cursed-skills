package com.jujutsu.api.buff.conditions;

import com.jujutsu.api.buff.BuffCondition;
import com.jujutsu.api.buff.BuffConditionType;
import com.jujutsu.registry.custom.BuffConditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;

public class TimerBuffCondition implements BuffCondition {
    public static final Codec<TimerBuffCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("maxTime").forGetter(TimerBuffCondition::getMaxTime),
                    Codec.INT.fieldOf("currentTime").forGetter(TimerBuffCondition::getCurrentTime))
            .apply(instance, TimerBuffCondition::new)
    );

    private final int maxTime;
    private int currentTime;

    public TimerBuffCondition(int time) {
        this.maxTime = time;
        currentTime = 0;
    }

    private TimerBuffCondition(int maxTime, int currentTime) {
        this.maxTime = maxTime;
        this.currentTime = currentTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    @Override
    public boolean test(LivingEntity entity) {
        return currentTime++ >= maxTime;
    }

    @Override
    public float getProgress(LivingEntity entity) {
        return (float) 1 / maxTime * currentTime;
    }

    @Override
    public BuffConditionType<?> getType() {
        return BuffConditions.TIMER;
    }
}
