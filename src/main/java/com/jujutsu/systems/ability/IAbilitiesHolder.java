package com.jujutsu.systems.ability;

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
}
