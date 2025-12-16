package com.jujutsu.systems.buff.conditions;

import com.jujutsu.systems.buff.BuffPredicate;
import com.jujutsu.systems.buff.BuffPredicateKey;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;

public class AttackCancellingCondition implements BuffPredicate {
    public static final Codec<AttackCancellingCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
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
    public float getProgress(LivingEntity entity) {
        return 0;
    }

    @Override
    public BuffPredicateKey<?> getKey() {
        return null;
    }
}
