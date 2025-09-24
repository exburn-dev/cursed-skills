package com.jujutsu.systems.ability.holder;

import com.jujutsu.systems.ability.core.AbilityInstance;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.jujutsu.systems.ability.core.AbilityType;
import com.jujutsu.systems.ability.passive.PassiveAbility;
import com.jujutsu.systems.ability.task.AbilityTask;
import com.jujutsu.systems.ability.upgrade.UpgradesData;
import net.minecraft.util.Identifier;

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

    void setUpgradesData(UpgradesData data);
    UpgradesData getUpgradesData();
    void setUpgradesId(Identifier id);
    Identifier getUpgradesId();

    void addAbilityTask(AbilityTask task);
}
