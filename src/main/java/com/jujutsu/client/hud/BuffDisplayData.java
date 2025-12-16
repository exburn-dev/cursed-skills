package com.jujutsu.client.hud;

import com.jujutsu.network.NbtPacketCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryEntry;

public record BuffDisplayData(RegistryEntry<EntityAttribute> attribute, BuffCancellingCondition condition) {
    public static final Codec<BuffDisplayData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityAttribute.CODEC.fieldOf("attribute").forGetter(BuffDisplayData::attribute),
            BuffCancellingCondition.CODEC.fieldOf("condition").forGetter(BuffDisplayData::condition))
            .apply(instance, BuffDisplayData::new));

    public static final PacketCodec<RegistryByteBuf, BuffDisplayData> PACKET_CODEC = new NbtPacketCodec<>(CODEC);
}
