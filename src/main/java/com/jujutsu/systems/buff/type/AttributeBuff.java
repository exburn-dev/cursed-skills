package com.jujutsu.systems.buff.type;

import com.jujutsu.systems.buff.BuffProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public record AttributeBuff(RegistryEntry<EntityAttribute> attribute, double amount, EntityAttributeModifier.Operation operation) implements BuffProvider {
    public static final MapCodec<AttributeBuff> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EntityAttribute.CODEC.fieldOf("attribute").forGetter(AttributeBuff::attribute),
            Codec.DOUBLE.fieldOf("amount").forGetter(AttributeBuff::amount),
            EntityAttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AttributeBuff::operation)
    ).apply(instance, AttributeBuff::new));

    @Override
    public void apply(LivingEntity entity, Identifier buffId) {
        EntityAttributeInstance instance = entity.getAttributes().getCustomInstance(attribute);
        if(instance != null && !instance.hasModifier(buffId)) {
            instance.addTemporaryModifier(new EntityAttributeModifier(buffId, amount, operation));
        }
    }

    @Override
    public void remove(LivingEntity entity, Identifier buffId) {
        EntityAttributeInstance instance = entity.getAttributes().getCustomInstance(attribute);
        if(instance != null) {
            instance.removeModifier(buffId);
        }
    }
}
