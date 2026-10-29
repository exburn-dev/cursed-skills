package com.jujutsu.api.buff.type;

import com.jujutsu.api.buff.BuffProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record AttributeBuff(Holder<Attribute> attribute, double amount,
                            AttributeModifier.Operation operation) implements BuffProvider {
    public static final MapCodec<AttributeBuff> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(AttributeBuff::attribute),
            Codec.DOUBLE.fieldOf("amount").forGetter(AttributeBuff::amount),
            AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AttributeBuff::operation)
    ).apply(instance, AttributeBuff::new));

    @Override
    public void apply(LivingEntity entity, Identifier buffId) {
        AttributeInstance instance = entity.getAttributes().getInstance(attribute);
        if(instance != null && !instance.hasModifier(buffId)) {
            instance.addTransientModifier(new AttributeModifier(buffId, amount, operation));
        }
    }

    @Override
    public void remove(LivingEntity entity, Identifier buffId) {
        AttributeInstance instance = entity.getAttributes().getInstance(attribute);
        if(instance != null) {
            instance.removeModifier(buffId);
        }
    }
}
