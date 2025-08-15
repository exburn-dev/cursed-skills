package com.jujutsu.network.payload;

import com.jujutsu.network.ModNetworkConstants;
import com.jujutsu.systems.ability.attribute.AbilityAttributesContainer;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SyncAbilityAttributesPayload(AbilityAttributesContainer container) implements CustomPayload {
    public static final Id<SyncAbilityAttributesPayload> ID = new Id<>(ModNetworkConstants.SYNC_ABILITY_ATTRIBUTES_ID);
    public static final PacketCodec<RegistryByteBuf, SyncAbilityAttributesPayload> CODEC = PacketCodec.tuple(
            AbilityAttributesContainer.PACKET_CODEC, SyncAbilityAttributesPayload::container,
            SyncAbilityAttributesPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
