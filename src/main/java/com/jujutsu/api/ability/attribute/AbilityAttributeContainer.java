package com.jujutsu.api.ability.attribute;

import com.jujutsu.Jujutsu;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public record AbilityAttributeContainer(
        Map<Holder<AbilityAttribute>, AbilityAttributeInstance> map) {

    public void addModifier(Holder<AbilityAttribute> attribute, Identifier id, double value, AbilityAttributeModifier.Type type) {
        map.put(attribute, new AbilityAttributeInstance(id, value, type));
    }

    public void addBaseModifier(Holder<AbilityAttribute> attribute, double value) {
        addModifier(attribute, Jujutsu.id("base"), value, AbilityAttributeModifier.Type.ADD);
    }

    public static AbilityAttributeContainer create() {
        return new AbilityAttributeContainer(new HashMap<>());
    }
}
