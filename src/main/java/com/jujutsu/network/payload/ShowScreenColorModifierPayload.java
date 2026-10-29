package com.jujutsu.network.payload;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.hud.ColorModifierHudRenderer;
import com.jujutsu.client.hud.CrosshairMarkRenderer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ShowScreenColorModifierPayload(ColorModifierHudRenderer.ColorModifierData colorModifier) implements CustomPacketPayload {
    public static final Identifier ID = Jujutsu.id("show_screen_color_modifier");
    public static final Type<ShowScreenColorModifierPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, ShowScreenColorModifierPayload> CODEC = StreamCodec.composite(
            StreamCodec.composite(
                    ByteBufCodecs.INT, ColorModifierHudRenderer.ColorModifierData::fadeIn,
                    ByteBufCodecs.INT, ColorModifierHudRenderer.ColorModifierData::hold,
                    ByteBufCodecs.INT, ColorModifierHudRenderer.ColorModifierData::fadeOut,
                    ByteBufCodecs.FLOAT, ColorModifierHudRenderer.ColorModifierData::maxStrength,
                    ByteBufCodecs.FLOAT, ColorModifierHudRenderer.ColorModifierData::maxBrightness,
                    ByteBufCodecs.INT, ColorModifierHudRenderer.ColorModifierData::color,
                    ColorModifierHudRenderer.ColorModifierData::new
            ), ShowScreenColorModifierPayload::colorModifier, ShowScreenColorModifierPayload::new);

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> {
            ColorModifierHudRenderer.addColorModifier(payload.colorModifier());
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
