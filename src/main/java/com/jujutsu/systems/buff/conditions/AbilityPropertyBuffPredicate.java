package com.jujutsu.systems.buff.conditions;

import com.jujutsu.systems.ability.core.AbilityComponent;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.jujutsu.systems.ability.data.AbilityProperty;
import com.jujutsu.systems.buff.BuffPredicate;
import com.jujutsu.systems.buff.BuffPredicateType;
import com.jujutsu.systems.buff.BuffPredicates;
import com.mojang.serialization.Codec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class AbilityPropertyBuffPredicate implements BuffPredicate {
    public static final Codec<AbilityPropertyBuffPredicate> CODEC = Codec.unit(AbilityPropertyBuffPredicate::new);

    private final AbilitySlot slot;
    private final AbilityProperty<Boolean> property;
    private final boolean reversedProperty;

    public AbilityPropertyBuffPredicate() {
        this.slot = null;
        this.property = null;
        this.reversedProperty = false;
    }

    public AbilityPropertyBuffPredicate(AbilitySlot slot, AbilityProperty<Boolean> property, boolean reversedProperty) {
        this.slot = slot;
        this.property = property;
        this.reversedProperty = reversedProperty;
    }

    @Override
    public boolean test(LivingEntity entity) {
        if(slot == null || property == null || !entity.isPlayer()) return true;
        boolean propertyValue = AbilityComponent.get((PlayerEntity) entity).getInstance(slot).get(property);

        return reversedProperty != propertyValue;
    }

    @Override
    public float getProgress(LivingEntity entity) {
        return 0;
    }

    @Override
    public BuffPredicateType<?> getType() {
        return BuffPredicates.ABILITY_PROPERTY;
    }
}
