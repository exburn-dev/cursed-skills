package com.jujutsu.systems.buff.type;

import com.jujutsu.registry.BuffTypes;
import com.jujutsu.systems.buff.BuffType;
import com.jujutsu.systems.buff.IBuff;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public record ConstantBuff(RegistryEntry<EntityAttribute> attribute, double amount, EntityAttributeModifier.Operation operation) implements IBuff {
    public static final MapCodec<ConstantBuff> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EntityAttribute.CODEC.fieldOf("attribute").forGetter(ConstantBuff::attribute),
            Codec.DOUBLE.fieldOf("amount").forGetter(ConstantBuff::amount),
            EntityAttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(ConstantBuff::operation)
    ).apply(instance, ConstantBuff::new));

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

    @Override
    public BuffType<?> getType() {
        return BuffTypes.CONSTANT_BUFF_TYPE;
    }
}
