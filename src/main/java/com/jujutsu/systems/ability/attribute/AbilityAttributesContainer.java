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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Optional;

public record AbilityAttributesContainer(HashMap<RegistryEntry<AbilityAttribute>, HashMap<Identifier, AbilityAttributeModifier>> attributes) {
    public static final Codec<AbilityAttributesContainer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(JujutsuRegistries.ABILITY_ATTRIBUTE.getEntryCodec(), Codec.unboundedMap(Identifier.CODEC, AbilityAttributeModifier.CODEC).xmap(HashMap::new, HashMap::new)).xmap(HashMap::new, HashMap::new)
                    .fieldOf("attributes").forGetter(AbilityAttributesContainer::attributes)
    ).apply(instance, AbilityAttributesContainer::new));

    public static final PacketCodec<RegistryByteBuf, AbilityAttributesContainer> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(HashMap::new, PacketCodecs.registryEntry(JujutsuRegistries.ABILITY_ATTRIBUTE_REGISTRY_KEY),
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

    public HashMap<Identifier, AbilityAttributeModifier> getModifiers(RegistryEntry<AbilityAttribute> attribute) {
        return attributes.get(attribute);
    }

    public void addModifier(RegistryEntry<AbilityAttribute> attribute, Identifier id, AbilityAttributeModifier modifier) {
        HashMap<Identifier, AbilityAttributeModifier> modifiers = getModifiers(attribute);
        if(modifiers != null) {
            modifiers.put(id, modifier);
            attributes().put(attribute, modifiers);
        }
        else {
            HashMap<Identifier, AbilityAttributeModifier> map = new HashMap<>();
            map.put(id, modifier);
            attributes().put(attribute, map);
        }
    }

    public static class Builder {
        private final HashMap<RegistryEntry<AbilityAttribute>, HashMap<Identifier, AbilityAttributeModifier>> map = new HashMap<>();

        public Builder addModifier(RegistryEntry<AbilityAttribute> attribute, Identifier id, double value, AbilityAttributeModifier.Type type) {
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

        public Builder addBaseModifier(RegistryEntry<AbilityAttribute> attribute, double value) {
            return addModifier(attribute, Jujutsu.getId("base"), value, AbilityAttributeModifier.Type.ADD);
        }

        public AbilityAttributesContainer build() {
            return new AbilityAttributesContainer(map);
        }
    }
}
