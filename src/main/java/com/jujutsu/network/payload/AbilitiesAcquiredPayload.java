package com.jujutsu.network.payload;

import com.jujutsu.systems.ability.AbilityType;
import com.jujutsu.network.ModNetworkConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;

public record AbilitiesAcquiredPayload(List<AbilityType> acquiredAbilities) implements CustomPayload {
    public static final Id<AbilitiesAcquiredPayload> ID = new Id<>(ModNetworkConstants.ABILITIES_ACQUIRED_ID);
    public static final PacketCodec<RegistryByteBuf, AbilitiesAcquiredPayload> CODEC = PacketCodec.tuple(PacketCodecs.registryCodec(AbilityType.CODEC).collect(PacketCodecs.toList()), AbilitiesAcquiredPayload::acquiredAbilities, AbilitiesAcquiredPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
