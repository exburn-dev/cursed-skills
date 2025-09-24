package com.jujutsu.systems.buff.conditions;

import com.jujutsu.registry.BuffTypes;
import com.jujutsu.systems.buff.BuffCancellingCondition;
import com.jujutsu.systems.buff.BuffCancellingConditionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;

public class AttackCancellingCondition implements BuffCancellingCondition {
    public static final MapCodec<AttackCancellingCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("entityLastAttackTime").forGetter(AttackCancellingCondition::getEntityLastAttackTime))
            .apply(instance, AttackCancellingCondition::new)
    );

    private final int entityLastAttackTime;

    public AttackCancellingCondition(int entityLastAttackTime) {
        this.entityLastAttackTime = entityLastAttackTime;
    }

    private int getEntityLastAttackTime() {
        return entityLastAttackTime;
    }

    @Override
    public boolean test(LivingEntity entity) {
        if(entity.getWorld().isClient()) return false;
        return entityLastAttackTime != entity.getLastAttackTime();
    }

    @Override
    public BuffCancellingConditionType<?> getType() {
        return BuffTypes.ATTACK_CANCELLING_CONDITION;
    }
}
