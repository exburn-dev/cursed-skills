package com.jujutsu.api.ability.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

@Deprecated
public record AbilityAttributeInstance(Identifier id, double amount, AbilityAttributeModifier.Type type) {
    public static final Codec<AbilityAttributeInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(AbilityAttributeInstance::id),
            Codec.DOUBLE.fieldOf("amount").forGetter(AbilityAttributeInstance::amount),
            AbilityAttributeModifier.Type.CODEC.fieldOf("operation").forGetter(AbilityAttributeInstance::type)
    ).apply(instance, AbilityAttributeInstance::new));
}