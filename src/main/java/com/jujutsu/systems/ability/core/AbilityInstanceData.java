package com.jujutsu.systems.ability.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record AbilityInstanceData(AbilityType type, AbilitySlot slot, AbilityStatus status, int useTime, int cooldownTime) {
    public static Codec<AbilityInstanceData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AbilityType.CODEC.fieldOf("type").forGetter(AbilityInstanceData::type),
            AbilitySlot.CODEC.fieldOf("slot").forGetter(AbilityInstanceData::slot),
            AbilityStatus.CODEC.fieldOf("status").forGetter(AbilityInstanceData::status),
            Codec.INT.fieldOf("useTime").forGetter(AbilityInstanceData::useTime),
            Codec.INT.fieldOf("cooldownTime").forGetter(AbilityInstanceData::cooldownTime)
    ).apply(instance, AbilityInstanceData::new));

    public static PacketCodec<RegistryByteBuf, AbilityInstanceData> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.registryCodec(AbilityType.CODEC), AbilityInstanceData::type,
            AbilitySlot.PACKET_CODEC, AbilityInstanceData::slot,
            AbilityStatus.PACKET_CODEC, AbilityInstanceData::status,
            PacketCodecs.INTEGER, AbilityInstanceData::useTime,
            PacketCodecs.INTEGER, AbilityInstanceData::cooldownTime,
            AbilityInstanceData::new
    );
}
