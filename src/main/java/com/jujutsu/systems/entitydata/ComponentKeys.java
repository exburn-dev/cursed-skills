package com.jujutsu.systems.entitydata;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.ability.core.AbilityComponent;
import com.jujutsu.systems.ability.passive.PassiveAbility;
import com.jujutsu.systems.ability.passive.PassiveAbilityComponent;

public class ComponentKeys {
    public static final EntityComponentKey<AbilityComponent> ABILITIES = new EntityComponentKey<>(
            Jujutsu.id("abilities"), AbilityComponent.class);

    public static final EntityComponentKey<PassiveAbilityComponent> PASSIVE_ABILITIES = new EntityComponentKey<>(
            Jujutsu.id("passive_abilities"), PassiveAbilityComponent.class);
}
