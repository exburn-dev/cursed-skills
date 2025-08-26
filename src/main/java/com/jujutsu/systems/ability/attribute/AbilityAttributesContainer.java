package com.jujutsu.systems.ability.attribute;

import com.jujutsu.Jujutsu;
import com.jujutsu.registry.JujutsuRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Optional;

public record AbilityAttributesContainer(HashMap<AbilityAttribute, HashMap<Identifier, AbilityAttributeModifier>> attributes) {
    public static final Codec<AbilityAttributesContainer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(JujutsuRegistries.ABILITY_ATTRIBUTE.getCodec(), Codec.unboundedMap(Identifier.CODEC, AbilityAttributeModifier.CODEC).xmap(HashMap::new, HashMap::new)).xmap(HashMap::new, HashMap::new)
                    .fieldOf("attributes").forGetter(AbilityAttributesContainer::attributes)
    ).apply(instance, AbilityAttributesContainer::new));

    public static final PacketCodec<RegistryByteBuf, AbilityAttributesContainer> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(HashMap::new, PacketCodecs.registryCodec(JujutsuRegistries.ABILITY_ATTRIBUTE.getCodec()),
                    PacketCodecs.map(HashMap::new, Identifier.PACKET_CODEC, AbilityAttributeModifier.PACKET_CODEC)), AbilityAttributesContainer::attributes,
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

    public static class Builder {
        private final HashMap<AbilityAttribute, HashMap<Identifier, AbilityAttributeModifier>> map = new HashMap<>();

        public Builder addModifier(AbilityAttribute attribute, Identifier id, double value, AbilityAttributeModifier.Type type) {
            HashMap<Identifier, AbilityAttributeModifier> modifiers = map.get(attribute);
            AbilityAttributeModifier modifier = new AbilityAttributeModifier(value, type);
            if(modifiers != null) {
                modifiers.put(id, modifier);
                map.put(attribute, modifiers);
            }
            else {
                HashMap<Identifier, AbilityAttributeModifier> newModifiers = new HashMap<>();
                newModifiers.put(id, modifier);
                map.put(attribute, newModifiers);
            }
            return this;
        }

        public Builder addBaseModifier(AbilityAttribute attribute, double value, AbilityAttributeModifier.Type type) {
            return addModifier(attribute, Jujutsu.getId("base"), value, type);
        }

        public AbilityAttributesContainer build() {
            return new AbilityAttributesContainer(map);
        }
    }
}
