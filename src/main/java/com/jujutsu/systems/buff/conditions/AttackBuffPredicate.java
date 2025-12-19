package com.jujutsu.systems.buff.conditions;

import com.jujutsu.systems.buff.BuffPredicate;
import com.jujutsu.systems.buff.BuffPredicateType;
import com.jujutsu.systems.buff.BuffPredicates;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;

public class AttackBuffPredicate implements BuffPredicate {
    public static final Codec<AttackBuffPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("entityLastAttackTime").forGetter(AttackBuffPredicate::getEntityLastAttackTime))
            .apply(instance, AttackBuffPredicate::new)
    );

    private final int entityLastAttackTime;

    public AttackBuffPredicate(int entityLastAttackTime) {
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
    public BuffPredicateType<?> getType() {
        return BuffPredicates.ATTACK;
    }
}
