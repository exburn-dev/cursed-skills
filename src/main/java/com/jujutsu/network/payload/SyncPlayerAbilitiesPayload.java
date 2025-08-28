package com.jujutsu.network.payload;

import com.jujutsu.systems.ability.holder.PlayerJujutsuAbilities;
import com.jujutsu.network.ModNetworkConstants;
import com.jujutsu.systems.ability.upgrade.UpgradesData;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncPlayerAbilitiesPayload(PlayerJujutsuAbilities abilities, UpgradesData upgradesData) implements CustomPayload {
    public static final CustomPayload.Id<SyncPlayerAbilitiesPayload> ID = new Id<>(ModNetworkConstants.SYNC_PLAYER_ABILITIES_ID);
    public static final PacketCodec<RegistryByteBuf, SyncPlayerAbilitiesPayload> CODEC = PacketCodec.tuple(
            PlayerJujutsuAbilities.PACKET_CODEC, SyncPlayerAbilitiesPayload::abilities,
            UpgradesData.PACKET_CODEC, SyncPlayerAbilitiesPayload::upgradesData,
            SyncPlayerAbilitiesPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
