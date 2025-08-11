package com.jujutsu.network.payload;

import com.jujutsu.network.ModNetworkConstants;
import com.jujutsu.systems.ability.AbilityAdditionalInput;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SyncAbilityAdditionalInputPayload(AbilityAdditionalInput additionalInput) implements CustomPayload {
    public static final Id<SyncAbilityAdditionalInputPayload> ID = new Id<>(ModNetworkConstants.SYNC_ABILITY_ADDITIONAL_INPUT_ID);
    public static final PacketCodec<RegistryByteBuf, SyncAbilityAdditionalInputPayload> CODEC = PacketCodec.tuple(
            AbilityAdditionalInput.PACKET_CODEC, SyncAbilityAdditionalInputPayload::additionalInput, SyncAbilityAdditionalInputPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
