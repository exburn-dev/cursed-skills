package com.jujutsu.systems.ability.attribute;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;

public interface AbilityAttributeContainerHolder {
    AbilityAttributesContainer getAbilityAttributes();
    void setAbilityAttributes(AbilityAttributesContainer container);
    HashMap<Identifier, AbilityAttributeModifier> getModifiers(AbilityAttribute attribute);
    void addModifier(AbilityAttribute attribute, Identifier id, AbilityAttributeModifier modifier);
}
