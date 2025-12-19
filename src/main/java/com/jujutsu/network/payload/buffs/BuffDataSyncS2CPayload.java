package com.jujutsu.network.payload.buffs;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.hud.BuffDisplayData;
import com.jujutsu.client.hud.BuffIconsRenderer;
import com.jujutsu.network.ModNetworkConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;

public record BuffDataSyncS2CPayload(List<BuffDisplayData> buffs) implements CustomPayload {
    public static final Identifier PACKET_ID = Jujutsu.id("sync_buffs");
    public static final Id<BuffDataSyncS2CPayload> ID = new Id<>(PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, BuffDataSyncS2CPayload> CODEC = PacketCodec.tuple(
            BuffDisplayData.PACKET_CODEC.collect(PacketCodecs.toList()), BuffDataSyncS2CPayload::buffs,
            BuffDataSyncS2CPayload::new);

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
            BuffIconsRenderer.setBuffs(payload.buffs());
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
