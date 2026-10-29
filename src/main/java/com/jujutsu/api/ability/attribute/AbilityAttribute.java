package com.jujutsu.api.ability.attribute;

import com.jujutsu.registry.JujutsuRegistries;
import net.minecraft.resources.Identifier;

public record AbilityAttribute(MeasureUnit unit) {
    public String getTranslationKey() {
        Identifier id = JujutsuRegistries.ABILITY_ATTRIBUTE.getKey(this);
        return id != null ? id.toLanguageKey("ability_attribute") : "";
    }

    public enum MeasureUnit {
        NUMBER(""),
        SECONDS("s"),
        METERS("m");

        private final String abbreviation;

        MeasureUnit(String abbreviation) {
            this.abbreviation = abbreviation;
        }

        public String getAbbreviation() {
            return abbreviation;
        }
    }
}
