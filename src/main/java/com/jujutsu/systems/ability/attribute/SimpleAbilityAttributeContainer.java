package com.jujutsu.systems.ability.attribute;

import com.jujutsu.Jujutsu;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public record SimpleAbilityAttributeContainer(
        Map<RegistryEntry<AbilityAttribute>, IdentifiedAbilityAttributeModifier> map) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<RegistryEntry<AbilityAttribute>, IdentifiedAbilityAttributeModifier> map = new HashMap<>();

        public Builder addModifier(RegistryEntry<AbilityAttribute> attribute, Identifier id, double value, AbilityAttributeModifier.Type type) {
            map.put(attribute, new IdentifiedAbilityAttributeModifier(id, value, type));
            return this;
        }

        public Builder addBaseModifier(RegistryEntry<AbilityAttribute> attribute, double value) {
            addModifier(attribute, Jujutsu.id("base"), value, AbilityAttributeModifier.Type.ADD);
            return this;
        }

        public SimpleAbilityAttributeContainer build() {
            return new SimpleAbilityAttributeContainer(map);
        }
    }
}
