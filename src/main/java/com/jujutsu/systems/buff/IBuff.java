package com.jujutsu.systems.buff;

import com.jujutsu.registry.JujutsuRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public interface IBuff {
    Codec<IBuff> CODEC = JujutsuRegistries.BUFF_TYPE.getCodec().dispatch(
            IBuff::getType, BuffType::codec);

    void apply(LivingEntity entity, Identifier buffId);
    void remove(LivingEntity entity, Identifier buffId);

    BuffType<?> getType();
}
