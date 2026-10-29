package com.jujutsu.client.input;

import com.jujutsu.api.ability.core.AbilitySlot;
import net.minecraft.client.KeyMapping;

public class AbilityKeyBinding extends KeyMapping {
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
