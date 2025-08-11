package com.jujutsu.network.payload;

import com.jujutsu.client.hud.ColorModifierHudRenderer;
import com.jujutsu.network.ModNetworkConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ShowScreenColorModifierPayload(ColorModifierHudRenderer.ColorModifierData colorModifier) implements CustomPayload {
    public static final CustomPayload.Id<ShowScreenColorModifierPayload> ID = new Id<>(ModNetworkConstants.SHOW_SCREEN_COLOR_MODIFIER_ID);
    public static final PacketCodec<RegistryByteBuf, ShowScreenColorModifierPayload> CODEC = PacketCodec.tuple(
            PacketCodec.tuple(
                    PacketCodecs.INTEGER, ColorModifierHudRenderer.ColorModifierData::fadeIn,
                    PacketCodecs.INTEGER, ColorModifierHudRenderer.ColorModifierData::hold,
                    PacketCodecs.INTEGER, ColorModifierHudRenderer.ColorModifierData::fadeOut,
                    PacketCodecs.FLOAT, ColorModifierHudRenderer.ColorModifierData::maxStrength,
                    PacketCodecs.FLOAT, ColorModifierHudRenderer.ColorModifierData::maxBrightness,
                    PacketCodecs.INTEGER, ColorModifierHudRenderer.ColorModifierData::color,
                    ColorModifierHudRenderer.ColorModifierData::new
            ), ShowScreenColorModifierPayload::colorModifier, ShowScreenColorModifierPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
