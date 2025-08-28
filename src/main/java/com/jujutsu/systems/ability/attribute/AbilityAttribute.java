package com.jujutsu.systems.ability.attribute;

import com.jujutsu.registry.JujutsuRegistries;
import net.minecraft.util.Identifier;

public record AbilityAttribute() {
    public String getTranslationKey() {
        Identifier id = JujutsuRegistries.ABILITY_ATTRIBUTE.getId(this);
        return id != null ? id.toTranslationKey("ability_attribute") : "";
    }
}
