package com.jujutsu.network.payload;

import com.jujutsu.network.ModNetworkConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundEvent;

public record PlayClientSoundPayload(SoundEvent sound) implements CustomPayload {
    public static final Id<PlayClientSoundPayload> ID = new Id<>(ModNetworkConstants.PLAY_CLIENT_SOUND_ID);
    public static final PacketCodec<RegistryByteBuf, PlayClientSoundPayload> CODEC = PacketCodec.tuple(
            SoundEvent.PACKET_CODEC, PlayClientSoundPayload::sound,
            PlayClientSoundPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
