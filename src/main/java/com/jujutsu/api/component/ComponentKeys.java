package com.jujutsu.api.component;

import com.jujutsu.Jujutsu;
import com.jujutsu.api.ability.attribute.AbilityAttributeComponent;
import com.jujutsu.api.ability.core.AbilityComponent;
import com.jujutsu.api.ability.passive.PassiveAbilityComponent;
import com.jujutsu.api.talent.TalentComponent;
import com.jujutsu.api.buff.BuffComponent;

public class ComponentKeys {
    public static final EntityComponentKey<AbilityComponent> ABILITIES = new EntityComponentKey<>(
            Jujutsu.id("abilities"), AbilityComponent.class);

    public static final EntityComponentKey<PassiveAbilityComponent> PASSIVE_ABILITIES = new EntityComponentKey<>(
            Jujutsu.id("passive_abilities"), PassiveAbilityComponent.class);

    public static final EntityComponentKey<BuffComponent> BUFFS = new EntityComponentKey<>(
            Jujutsu.id("buffs"), BuffComponent.class);

    public static final EntityComponentKey<AbilityAttributeComponent> ABILITY_ATTRIBUTES = new EntityComponentKey<>(
            Jujutsu.id("ability_attributes"), AbilityAttributeComponent.class);

    public static final EntityComponentKey<TalentComponent> TALENTS = new EntityComponentKey<>(
            Jujutsu.id("talents"), TalentComponent.class);

}
