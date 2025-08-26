package com.jujutsu.systems.ability.upgrade;

import com.jujutsu.systems.ability.attribute.AbilityAttributesContainer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record AbilityUpgrade(Identifier id, AbilityAttributesContainer container) {
    public static final Codec<AbilityUpgrade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(AbilityUpgrade::id),
            AbilityAttributesContainer.CODEC.fieldOf("container").forGetter(AbilityUpgrade::container)
    ).apply(instance, AbilityUpgrade::new));

    public static final PacketCodec<RegistryByteBuf, AbilityUpgrade> PACKET_CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, AbilityUpgrade::id,
            AbilityAttributesContainer.PACKET_CODEC, AbilityUpgrade::container,
            AbilityUpgrade::new);
}
