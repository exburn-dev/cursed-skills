package com.jujutsu.systems.buff;

import com.jujutsu.registry.JujutsuRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.entity.LivingEntity;

public interface BuffCancellingCondition {
    Codec<BuffCancellingCondition> CODEC = JujutsuRegistries.BUFF_CANCELLING_CONDITION_TYPE.getCodec()
            .dispatch("type", BuffCancellingCondition::getType, BuffCancellingConditionType::codec);

    boolean test(LivingEntity entity);

    BuffCancellingConditionType<?> getType();

    default float getProgress(LivingEntity entity) {
        return 0;
    }
}
