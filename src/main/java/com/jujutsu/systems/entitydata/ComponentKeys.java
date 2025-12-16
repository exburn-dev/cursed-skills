package com.jujutsu.systems.entitydata;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.ability.core.AbilityComponent;

public class ComponentKeys {
    public static final EntityComponentKey<AbilityComponent> ABILITIES = new EntityComponentKey<>(
            Jujutsu.id("abilities"), AbilityComponent.class);

    public static final EntityComponentKey<AbilityComponent> PASSIVE_ABILITIES = new EntityComponentKey<>(
            Jujutsu.id("passive_abilities"), AbilityComponent.class);
}
