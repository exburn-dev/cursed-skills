package com.jujutsu.api.buff.conditions;

import com.jujutsu.api.buff.BuffCondition;
import com.jujutsu.api.buff.BuffConditionType;
import com.jujutsu.registry.custom.BuffConditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;

public class AttackBuffCondition implements BuffCondition {
    public static final Codec<AttackBuffCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("entityLastAttackTime").forGetter(AttackBuffCondition::getEntityLastAttackTime))
            .apply(instance, AttackBuffCondition::new)
    );

    private final int entityLastAttackTime;

    public AttackBuffCondition(int entityLastAttackTime) {
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
    public float getProgress(LivingEntity entity) {
        return 0;
    }

    @Override
    public BuffConditionType<?> getType() {
        return BuffConditions.ATTACK;
    }
}
