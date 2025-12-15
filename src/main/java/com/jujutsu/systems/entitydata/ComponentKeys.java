package com.jujutsu.systems.entitydata;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.ability.core.AbilityComponent;

public class ComponentKeys {
    public static final EntityComponentKey<AbilityComponent> ABILITIES = new EntityComponentKey<>(
            Jujutsu.id("abilities"), AbilityComponent.class);
}
