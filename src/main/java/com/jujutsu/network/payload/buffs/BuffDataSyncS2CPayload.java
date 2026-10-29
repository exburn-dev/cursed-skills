package com.jujutsu.network.payload.buffs;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.hud.BuffDisplayData;
import com.jujutsu.client.hud.BuffIconsRenderer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.List;

public record BuffDataSyncS2CPayload(List<BuffDisplayData> buffs) implements CustomPacketPayload {
    public static final Identifier PACKET_ID = Jujutsu.id("sync_buffs");
    public static final Type<BuffDataSyncS2CPayload> TYPE = new Type<>(PACKET_ID);

    public static final StreamCodec<FriendlyByteBuf, BuffDataSyncS2CPayload> CODEC = StreamCodec.composite(
            BuffDisplayData.STREAM_CODEC.apply(ByteBufCodecs.list()), BuffDataSyncS2CPayload::buffs,
            BuffDataSyncS2CPayload::new);

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> {
            BuffIconsRenderer.setBuffs(payload.buffs());
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
