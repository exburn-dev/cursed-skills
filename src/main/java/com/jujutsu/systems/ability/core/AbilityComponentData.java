package com.jujutsu.systems.ability.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record AbilityComponentData(AbilityType type, AbilitySlot slot, AbilityStatus status, int useTime, int cooldownTime) {
    public static Codec<AbilityComponentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AbilityType.CODEC.fieldOf("type").forGetter(AbilityComponentData::type),
            AbilitySlot.CODEC.fieldOf("slot").forGetter(AbilityComponentData::slot),
            AbilityStatus.CODEC.fieldOf("status").forGetter(AbilityComponentData::status),
            Codec.INT.fieldOf("useTime").forGetter(AbilityComponentData::useTime),
            Codec.INT.fieldOf("cooldownTime").forGetter(AbilityComponentData::cooldownTime)
    ).apply(instance, AbilityComponentData::new));
}
