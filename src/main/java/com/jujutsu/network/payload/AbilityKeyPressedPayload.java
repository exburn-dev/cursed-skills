package com.jujutsu.network.payload;

import com.jujutsu.systems.ability.AbilitySlot;
import com.jujutsu.network.ModNetworkConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record AbilityKeyPressedPayload(AbilitySlot abilitySlot, boolean cancel) implements CustomPayload {
    public static final CustomPayload.Id<AbilityKeyPressedPayload> ID = new Id<>(ModNetworkConstants.ABILITY_KEY_PRESSED_ID);
    public static final PacketCodec<RegistryByteBuf, AbilityKeyPressedPayload> CODEC = PacketCodec.tuple(AbilitySlot.PACKET_CODEC, AbilityKeyPressedPayload::abilitySlot, PacketCodecs.BOOL, AbilityKeyPressedPayload::cancel, AbilityKeyPressedPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
