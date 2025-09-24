package com.jujutsu.client.keybind;

import com.jujutsu.systems.ability.core.AbilitySlot;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class AbilityKeyBinding extends KeyBinding {
    private final AbilitySlot abilitySlot;

    public AbilityKeyBinding(AbilitySlot abilitySlot, String translationKey, int code, String category) {
        super(translationKey, code, category);
        this.abilitySlot = abilitySlot;
    }

    public AbilityKeyBinding(AbilitySlot abilitySlot, String translationKey, InputUtil.Type type, int code, String category) {
        super(translationKey, type, code, category);
        this.abilitySlot = abilitySlot;
    }

    public AbilitySlot getAbilitySlot() {
        return abilitySlot;
    }
}
