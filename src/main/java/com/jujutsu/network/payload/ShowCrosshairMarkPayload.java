package com.jujutsu.network.payload;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.hud.CrosshairMarkRenderer;
import com.jujutsu.client.hud.CrosshairMarkRenderer.CrosshairMarkData;
import com.jujutsu.client.hud.FlashSystemHudRenderer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ShowCrosshairMarkPayload(CrosshairMarkData markData) implements CustomPacketPayload {
    public static final Identifier ID = Jujutsu.id("show_crosshair_mark");
    public static final Type<ShowCrosshairMarkPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, ShowCrosshairMarkPayload> CODEC = StreamCodec.composite(
            StreamCodec.composite(
                    ByteBufCodecs.INT, CrosshairMarkData::fadeIn,
                    ByteBufCodecs.INT, CrosshairMarkData::hold,
                    ByteBufCodecs.INT, CrosshairMarkData::fadeOut,
                    ByteBufCodecs.INT, CrosshairMarkData::color,
                    CrosshairMarkData::new
            ), ShowCrosshairMarkPayload::markData, ShowCrosshairMarkPayload::new);

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> {
            CrosshairMarkRenderer.addMarkData(payload.markData());
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
