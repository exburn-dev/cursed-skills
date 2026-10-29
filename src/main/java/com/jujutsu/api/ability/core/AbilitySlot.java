package com.jujutsu.api.ability.core;

import com.jujutsu.registry.JujutsuRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

public final class AbilitySlot {
    public static final Codec<AbilitySlot> CODEC = JujutsuRegistries.ABILITY_SLOT.byNameCodec();
    public static final StreamCodec<RegistryFriendlyByteBuf, AbilitySlot> STREAM_CODEC = ByteBufCodecs.registry(JujutsuRegistries.ABILITY_SLOT_KEY);

    public final ResourceKey<AbilitySlot> key;
    public final Activation activation;

    public AbilitySlot(ResourceKey<AbilitySlot> resourceKey, Activation activationType) {
        this.key = resourceKey;
        this.activation = activationType;
    }

    public boolean isManual() {
        return activation == Activation.MANUAL;
    }

    public boolean isTriggered() {
        return activation == Activation.TRIGGERED;
    }

    @Override
    public String toString() {
        return key.toString();
    }

    public enum Activation {
        MANUAL,
        TRIGGERED,
    }
}