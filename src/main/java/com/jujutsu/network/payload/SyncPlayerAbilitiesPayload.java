package com.jujutsu.network.payload;

import com.jujutsu.systems.ability.PlayerJujutsuAbilities;
import com.jujutsu.network.ModNetworkConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SyncPlayerAbilitiesPayload(PlayerJujutsuAbilities abilities) implements CustomPayload {
    public static final CustomPayload.Id<SyncPlayerAbilitiesPayload> ID = new Id<>(ModNetworkConstants.SYNC_PLAYER_ABILITIES_ID);
    public static final PacketCodec<RegistryByteBuf, SyncPlayerAbilitiesPayload> CODEC = PacketCodec.tuple(PlayerJujutsuAbilities.PACKET_CODEC, SyncPlayerAbilitiesPayload::abilities, SyncPlayerAbilitiesPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
