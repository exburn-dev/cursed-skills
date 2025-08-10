package com.jujutsu.network.payload;

import com.jujutsu.network.ModNetworkConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import com.jujutsu.client.hud.FlashSystemHudRenderer.FlashData;

public record ShowScreenFlashPayload(FlashData flashData) implements CustomPayload {
    public static final Id<ShowScreenFlashPayload> ID = new Id<>(ModNetworkConstants.SHOW_SCREEN_FLASH_ID);
    public static final PacketCodec<RegistryByteBuf, ShowScreenFlashPayload> CODEC = PacketCodec.tuple(
            PacketCodec.tuple(
                    PacketCodecs.INTEGER, FlashData::fadeIn,
                    PacketCodecs.INTEGER, FlashData::hold,
                    PacketCodecs.INTEGER, FlashData::fadeOut,
                    PacketCodecs.FLOAT, FlashData::maxAlpha,
                    PacketCodecs.INTEGER, FlashData::color, FlashData::new),
            ShowScreenFlashPayload::flashData, ShowScreenFlashPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
