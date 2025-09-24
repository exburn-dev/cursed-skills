package com.jujutsu.systems.ability.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record AbilityAdditionalInput(int keyCode, int scanCode, int mouseButton, int timeout, boolean showOnScreen) {
    public static final Codec<AbilityAdditionalInput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("keyCode").forGetter(AbilityAdditionalInput::keyCode),
            Codec.INT.fieldOf("scanCode").forGetter(AbilityAdditionalInput::scanCode),
            Codec.INT.fieldOf("mouseButton").forGetter(AbilityAdditionalInput::mouseButton),
            Codec.INT.fieldOf("timeout").forGetter(AbilityAdditionalInput::timeout),
            Codec.BOOL.fieldOf("showOnScreen").forGetter(AbilityAdditionalInput::showOnScreen)
    ).apply(instance, AbilityAdditionalInput::new));

    public static final PacketCodec<RegistryByteBuf, AbilityAdditionalInput> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, AbilityAdditionalInput::keyCode,
            PacketCodecs.INTEGER, AbilityAdditionalInput::scanCode,
            PacketCodecs.INTEGER, AbilityAdditionalInput::mouseButton,
            PacketCodecs.INTEGER, AbilityAdditionalInput::timeout,
            PacketCodecs.BOOL, AbilityAdditionalInput::showOnScreen,
            AbilityAdditionalInput::new);
}
