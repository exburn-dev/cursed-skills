package com.jujutsu.systems.ability.attribute;

import com.google.common.collect.ImmutableList;
import com.jujutsu.Jujutsu;
import com.jujutsu.registry.JujutsuRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public record AbilityAttributesContainer(HashMap<AbilityAttribute, ArrayList<AbilityAttributeModifier>> attributes) {
    public static final Codec<AbilityAttributesContainer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(JujutsuRegistries.ABILITY_ATTRIBUTE.getCodec(), AbilityAttributeModifier.CODEC.listOf().xmap(ArrayList::new, ImmutableList::copyOf)).xmap(HashMap::new, HashMap::new)
                    .fieldOf("attributes").forGetter(AbilityAttributesContainer::attributes)
    ).apply(instance, AbilityAttributesContainer::new));

    public static final PacketCodec<RegistryByteBuf, AbilityAttributesContainer> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(HashMap::new, PacketCodecs.registryCodec(JujutsuRegistries.ABILITY_ATTRIBUTE.getCodec()),
                    AbilityAttributeModifier.PACKET_CODEC.collect(PacketCodecs.toCollection(ArrayList::new))), AbilityAttributesContainer::attributes,
            AbilityAttributesContainer::new);

    public <T> T serialize(DynamicOps<T> ops) {
        Optional<T> optional = CODEC.encodeStart(ops, this).resultOrPartial((error) -> {
            Jujutsu.LOGGER.warn("Failed to serialize AbilityAttributesContainer: {}", error);
        });
        return optional.orElseGet(ops::emptyList);
    }

    public static AbilityAttributesContainer deserialize(Dynamic<?> dynamic) {
        var optional = CODEC.decode(dynamic).resultOrPartial((error) -> {
            Jujutsu.LOGGER.warn("Failed to deserialize AbilityAttributesContainer: {}", error);
        });
        if(optional.isEmpty()) {
            return new AbilityAttributesContainer(new HashMap<>());
        }
        return optional.get().getFirst();
    }
}
