package com.jujutsu.systems.ability.attribute;

import com.jujutsu.registry.JujutsuRegistries;
import net.minecraft.util.Identifier;

public record AbilityAttribute(MeasureUnit unit) {
    public String getMeasureUnitSymbol() {
        return switch (unit) {
            case SECONDS -> "s";
            case METERS -> "m";
            default -> "";
        };
    }

    public String getTranslationKey() {
        Identifier id = JujutsuRegistries.ABILITY_ATTRIBUTE.getId(this);
        return id != null ? id.toTranslationKey("ability_attribute") : "";
    }

    public enum MeasureUnit {
        NUMBER,
        SECONDS,
        METERS;
    }
}
