package com.jujutsu.network.payload;

import com.jujutsu.client.hud.CrosshairMarkRenderer;
import com.jujutsu.client.hud.CrosshairMarkRenderer.CrosshairMarkData;
import com.jujutsu.network.ModNetworkConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ShowCrosshairMarkPayload(CrosshairMarkData markData) implements CustomPayload {
    public static final Id<ShowCrosshairMarkPayload> ID = new Id<>(ModNetworkConstants.SHOW_CROSSHAIR_MARK_ID);
    public static final PacketCodec<RegistryByteBuf, ShowCrosshairMarkPayload> CODEC = PacketCodec.tuple(
            PacketCodec.tuple(
                    PacketCodecs.INTEGER, CrosshairMarkData::fadeIn,
                    PacketCodecs.INTEGER, CrosshairMarkData::hold,
                    PacketCodecs.INTEGER, CrosshairMarkData::fadeOut,
                    PacketCodecs.INTEGER, CrosshairMarkData::color,
                    CrosshairMarkData::new
            ), ShowCrosshairMarkPayload::markData, ShowCrosshairMarkPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
