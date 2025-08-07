package com.jujutsu.network.payload;

import com.jujutsu.network.ModNetworkConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record OpenHandSettingScreenPayload(boolean before) implements CustomPayload {
    public static final CustomPayload.Id<OpenHandSettingScreenPayload> ID = new Id<>(ModNetworkConstants.OPEN_HAND_SETTING_SCREEN_ID);
    public static final PacketCodec<RegistryByteBuf, OpenHandSettingScreenPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, OpenHandSettingScreenPayload::before, OpenHandSettingScreenPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
