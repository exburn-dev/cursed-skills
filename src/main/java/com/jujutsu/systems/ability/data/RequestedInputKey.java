package com.jujutsu.systems.ability.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record RequestedInputKey(int keyCode, int mouseButton) {
    public static final Codec<RequestedInputKey> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("keyCode").forGetter(RequestedInputKey::keyCode),
            Codec.INT.fieldOf("mouseButton").forGetter(RequestedInputKey::mouseButton)
    ).apply(instance, RequestedInputKey::new));

    public static final PacketCodec<RegistryByteBuf, RequestedInputKey> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, RequestedInputKey::keyCode,
            PacketCodecs.INTEGER, RequestedInputKey::mouseButton,
            RequestedInputKey::new);
}
