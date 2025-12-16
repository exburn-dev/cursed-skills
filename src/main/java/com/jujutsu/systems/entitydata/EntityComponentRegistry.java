package com.jujutsu.systems.entitydata;

import com.jujutsu.mixinterface.EntityComponentsAccessor;
import com.jujutsu.systems.ability.core.AbilityComponent;
import com.jujutsu.systems.ability.passive.PassiveAbilityComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class EntityComponentRegistry {
    public static void attach(LivingEntity entity) {
        if(entity.isPlayer()) {
            attachPlayer((PlayerEntity) entity);
        }
    }

    public static void attachPlayer(PlayerEntity player) {
        AbilityComponent abilities = new AbilityComponent(player);
        PassiveAbilityComponent passiveAbilities = new PassiveAbilityComponent(player);

        ((EntityComponentsAccessor) player).jujutsu$getContainer().add(ComponentKeys.ABILITIES, abilities);
        ((EntityComponentsAccessor) player).jujutsu$getContainer().add(ComponentKeys.PASSIVE_ABILITIES, passiveAbilities);
    }
}
