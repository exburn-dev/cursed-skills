package com.jujutsu.systems.buff.conditions;

import com.jujutsu.registry.BuffTypes;
import com.jujutsu.systems.buff.BuffCancellingCondition;
import com.jujutsu.systems.buff.BuffCancellingConditionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;

public class TimeCancellingCondition implements BuffCancellingCondition {
    public static final MapCodec<TimeCancellingCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("maxTime").forGetter(TimeCancellingCondition::getMaxTime), Codec.INT.fieldOf("currentTime").forGetter(TimeCancellingCondition::getCurrentTime))
            .apply(instance, TimeCancellingCondition::new)
    );

    private final int maxTime;
    private int currentTime;

    public TimeCancellingCondition(int time) {
        this.maxTime = time;
        currentTime = 0;
    }

    private TimeCancellingCondition(int maxTime, int currentTime) {
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
    public BuffCancellingConditionType<?> getType() {
        return BuffTypes.TIME_CANCELLING_CONDITION;
    }
}
