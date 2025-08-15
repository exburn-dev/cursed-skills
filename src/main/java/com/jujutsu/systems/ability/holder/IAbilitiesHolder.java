package com.jujutsu.systems.ability.holder;

import com.jujutsu.systems.ability.AbilityInstance;
import com.jujutsu.systems.ability.AbilitySlot;
import com.jujutsu.systems.ability.AbilityType;
import com.jujutsu.systems.ability.attribute.AbilityAttribute;
import com.jujutsu.systems.ability.attribute.AbilityAttributeModifier;
import com.jujutsu.systems.ability.passive.PassiveAbility;

import java.util.List;

public interface IAbilitiesHolder {
    AbilityInstance getAbilityInstance(AbilitySlot slot);
    void addAbilityInstance(AbilityInstance instance, AbilitySlot slot);
    void removeAbilityInstance(AbilitySlot slot);

    void runAbility(AbilitySlot slot);
    void tryCancelAbility(AbilitySlot slot);

    List<AbilitySlot> getSlots();
    List<AbilitySlot> getRunningSlots();
    List<PassiveAbility> getPassiveAbilities();

    boolean isRunning(AbilityType type);
    boolean onCooldown(AbilitySlot slot);

    void addPassiveAbility(PassiveAbility instance);
    void removePassiveAbility(PassiveAbility instance);

    List<AbilityAttributeModifier> getModifiers(AbilityAttribute attribute);
    void addModifier(AbilityAttribute attribute, AbilityAttributeModifier modifier);
}
