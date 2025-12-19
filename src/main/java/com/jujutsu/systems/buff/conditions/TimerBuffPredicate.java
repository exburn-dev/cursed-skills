package com.jujutsu.systems.buff.conditions;

import com.jujutsu.systems.buff.BuffPredicate;
import com.jujutsu.systems.buff.BuffPredicateType;
import com.jujutsu.systems.buff.BuffPredicates;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;

public class TimerBuffPredicate implements BuffPredicate {
    public static final Codec<TimerBuffPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("maxTime").forGetter(TimerBuffPredicate::getMaxTime),
                    Codec.INT.fieldOf("currentTime").forGetter(TimerBuffPredicate::getCurrentTime))
            .apply(instance, TimerBuffPredicate::new)
    );

    private final int maxTime;
    private int currentTime;

    public TimerBuffPredicate(int time) {
        this.maxTime = time;
        currentTime = 0;
    }

    private TimerBuffPredicate(int maxTime, int currentTime) {
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
    public BuffPredicateType<?> getType() {
        return BuffPredicates.TIMER;
    }
}
