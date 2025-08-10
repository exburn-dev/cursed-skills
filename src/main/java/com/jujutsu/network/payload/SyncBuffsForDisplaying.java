package com.jujutsu.network.payload;

import com.jujutsu.client.hud.BuffDisplayData;
import com.jujutsu.network.ModNetworkConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;

public record SyncBuffsForDisplaying(List<BuffDisplayData> buffs) implements CustomPayload {
    public static final Id<SyncBuffsForDisplaying> ID = new Id<>(ModNetworkConstants.SYNC_BUFFS_FOR_DISPLAYING_ID);
    public static final PacketCodec<RegistryByteBuf, SyncBuffsForDisplaying> CODEC = PacketCodec.tuple(
            BuffDisplayData.PACKET_CODEC.collect(PacketCodecs.toList()), SyncBuffsForDisplaying::buffs,
            SyncBuffsForDisplaying::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
