package com.jujutsu.api.buff.conditions;

import com.jujutsu.api.ability.core.AbilityComponent;
import com.jujutsu.api.ability.core.AbilitySlot;
import com.jujutsu.api.ability.data.AbilityProperty;
import com.jujutsu.api.buff.BuffCondition;
import com.jujutsu.api.buff.BuffConditionType;
import com.jujutsu.registry.custom.BuffConditions;
import com.mojang.serialization.Codec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class AbilityPropertyBuffCondition implements BuffCondition {
    public static final Codec<AbilityPropertyBuffCondition> CODEC = Codec.unit(AbilityPropertyBuffCondition::new);

    private final AbilitySlot slot;
    private final AbilityProperty<Boolean> property;
    private final boolean reversedProperty;

    public AbilityPropertyBuffCondition() {
        this.slot = null;
        this.property = null;
        this.reversedProperty = false;
    }

    public AbilityPropertyBuffCondition(AbilitySlot slot, AbilityProperty<Boolean> property, boolean reversedProperty) {
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
    public BuffConditionType<?> getType() {
        return BuffConditions.ABILITY_PROPERTY;
    }
}
