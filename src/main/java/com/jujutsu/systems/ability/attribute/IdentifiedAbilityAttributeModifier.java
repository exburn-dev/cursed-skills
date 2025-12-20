package com.jujutsu.systems.ability.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

public record IdentifiedAbilityAttributeModifier(Identifier id, double amount, AbilityAttributeModifier.Type type) {
    public static final Codec<IdentifiedAbilityAttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(IdentifiedAbilityAttributeModifier::id),
            Codec.DOUBLE.fieldOf("amount").forGetter(IdentifiedAbilityAttributeModifier::amount),
            AbilityAttributeModifier.Type.CODEC.fieldOf("operation").forGetter(IdentifiedAbilityAttributeModifier::type)
    ).apply(instance, IdentifiedAbilityAttributeModifier::new));
}