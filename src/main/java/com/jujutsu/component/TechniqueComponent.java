package com.jujutsu.component;

import com.jujutsu.systems.ability.AbilitySlot;
import com.jujutsu.systems.ability.AbilityType;
import com.jujutsu.systems.ability.PassiveAbility;
import com.jujutsu.registry.JujutsuRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record TechniqueComponent(Map<AbilitySlot, AbilityType> abilities, List<PassiveAbility> passiveAbilities) {
    public static final Codec<TechniqueComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.unboundedMap(AbilitySlot.CODEC,
                    JujutsuRegistries.ABILITY_TYPE.getCodec()).fieldOf("abilities").forGetter(TechniqueComponent::abilities),
                    PassiveAbility.CODEC.listOf().fieldOf("passiveAbilities").forGetter(TechniqueComponent::passiveAbilities))
                    .apply(instance, TechniqueComponent::new));

    public static final PacketCodec<RegistryByteBuf, TechniqueComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(HashMap::new, AbilitySlot.PACKET_CODEC, PacketCodecs.registryCodec(JujutsuRegistries.ABILITY_TYPE.getCodec())), TechniqueComponent::abilities,
            PassiveAbility.PACKET_CODEC.collect(PacketCodecs.toList()), TechniqueComponent::passiveAbilities,
            TechniqueComponent::new);
}
