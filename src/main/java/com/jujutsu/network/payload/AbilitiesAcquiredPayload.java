package com.jujutsu.network.payload;

import com.jujutsu.registry.JujutsuRegistries;
import com.jujutsu.api.ability.core.AbilityType;
import com.jujutsu.network.ModNetworkConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record AbilitiesAcquiredPayload(List<AbilityType> acquiredAbilities) implements CustomPacketPayload {
    public static final Type<AbilitiesAcquiredPayload> TYPE = new Type<>(ModNetworkConstants.ABILITIES_ACQUIRED_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, AbilitiesAcquiredPayload> CODEC =
            StreamCodec.composite(ByteBufCodecs.registry(JujutsuRegistries.ABILITY_TYPE_KEY)
                    .apply(ByteBufCodecs.list()), AbilitiesAcquiredPayload::acquiredAbilities, AbilitiesAcquiredPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return null;
    }
}
