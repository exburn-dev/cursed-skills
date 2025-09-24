package com.jujutsu.network.payload;

import com.jujutsu.network.ModNetworkConstants;
import com.jujutsu.systems.ability.data.AbilityAdditionalInput;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record AdditionalInputPressedPayload(AbilityAdditionalInput additionalInput) implements CustomPayload {
    public static final Id<AdditionalInputPressedPayload> ID = new Id<>(ModNetworkConstants.ADDITIONAL_INPUT_PRESSED_ID);
    public static final PacketCodec<RegistryByteBuf, AdditionalInputPressedPayload> CODEC = PacketCodec.tuple(
            AbilityAdditionalInput.PACKET_CODEC, AdditionalInputPressedPayload::additionalInput, AdditionalInputPressedPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
