package com.jujutsu.network.payload;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.hud.FlashSystemHudRenderer;
import com.jujutsu.client.hud.FlashSystemHudRenderer.FlashData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ShowScreenFlashPayload(FlashData flashData) implements CustomPacketPayload {
    public static final Identifier ID = Jujutsu.id("show_screen_flash");
    public static final Type<ShowScreenFlashPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, ShowScreenFlashPayload> CODEC = StreamCodec.composite(
            StreamCodec.composite(
                    ByteBufCodecs.INT, FlashData::fadeIn,
                    ByteBufCodecs.INT, FlashData::hold,
                    ByteBufCodecs.INT, FlashData::fadeOut,
                    ByteBufCodecs.FLOAT, FlashData::maxAlpha,
                    ByteBufCodecs.INT, FlashData::color, FlashData::new),
            ShowScreenFlashPayload::flashData, ShowScreenFlashPayload::new);

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> {
            FlashSystemHudRenderer.addFlashData(payload.flashData());
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
