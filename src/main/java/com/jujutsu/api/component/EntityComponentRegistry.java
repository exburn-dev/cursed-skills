package com.jujutsu.api.component;

import com.jujutsu.mixinterface.EntityComponentsAccessor;
import com.jujutsu.api.ability.attribute.AbilityAttributeComponent;
import com.jujutsu.api.ability.core.AbilityComponent;
import com.jujutsu.api.ability.passive.PassiveAbilityComponent;
import com.jujutsu.api.talent.TalentComponent;
import com.jujutsu.api.buff.BuffComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class EntityComponentRegistry {
    public static void attach(LivingEntity entity) {
        if(entity.isPlayer()) {
            attachPlayer((PlayerEntity) entity);
        }

        attachEntity(entity);
    }

    public static void attachPlayer(PlayerEntity player) {
        EntityComponentsAccessor accessor = (EntityComponentsAccessor) player;

        AbilityComponent abilities = new AbilityComponent(player);
        PassiveAbilityComponent passiveAbilities = new PassiveAbilityComponent(player);
        AbilityAttributeComponent attributeComponent = new AbilityAttributeComponent(player);
        TalentComponent upgradesComponent = new TalentComponent(player);

        accessor.jujutsu$getContainer().add(ComponentKeys.ABILITIES, abilities);
        accessor.jujutsu$getContainer().add(ComponentKeys.PASSIVE_ABILITIES, passiveAbilities);
        accessor.jujutsu$getContainer().add(ComponentKeys.ABILITY_ATTRIBUTES, attributeComponent);
        accessor.jujutsu$getContainer().add(ComponentKeys.TALENTS, upgradesComponent);
    }

    public static void attachEntity(LivingEntity entity) {
        EntityComponentsAccessor accessor = (EntityComponentsAccessor) entity;
        BuffComponent buffs = new BuffComponent(entity);

        accessor.jujutsu$getContainer().add(ComponentKeys.BUFFS, buffs);
    }
}
