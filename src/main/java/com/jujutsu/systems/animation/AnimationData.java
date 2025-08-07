package com.jujutsu.systems.animation;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record AnimationData(UUID playerUUID, Identifier animation, int priority) {
    public static final PacketCodec<RegistryByteBuf, AnimationData> PACKET_CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, AnimationData::playerUUID,
            Identifier.PACKET_CODEC, AnimationData::animation,
            PacketCodecs.INTEGER, AnimationData::priority,
            AnimationData::new);
}
