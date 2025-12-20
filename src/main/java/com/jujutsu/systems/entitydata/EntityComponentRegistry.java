package com.jujutsu.systems.entitydata;

import com.jujutsu.mixinterface.EntityComponentsAccessor;
import com.jujutsu.systems.ability.attribute.AbilityAttributeComponent;
import com.jujutsu.systems.ability.core.AbilityComponent;
import com.jujutsu.systems.ability.passive.PassiveAbilityComponent;
import com.jujutsu.systems.talent.TalentsComponent;
import com.jujutsu.systems.buff.BuffComponent;
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
        TalentsComponent upgradesComponent = new TalentsComponent(player);

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
