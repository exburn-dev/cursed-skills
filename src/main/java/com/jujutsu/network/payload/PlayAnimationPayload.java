package com.jujutsu.network.payload;

import com.jujutsu.network.ModNetworkConstants;
import com.jujutsu.systems.animation.AnimationData;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record PlayAnimationPayload(AnimationData animationData) implements CustomPayload {
    public static final Id<PlayAnimationPayload> ID = new Id<>(ModNetworkConstants.PLAY_ANIMATION_ID);

    public static final PacketCodec<RegistryByteBuf, PlayAnimationPayload> CODEC = PacketCodec.tuple(
            AnimationData.PACKET_CODEC, PlayAnimationPayload::animationData, PlayAnimationPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
