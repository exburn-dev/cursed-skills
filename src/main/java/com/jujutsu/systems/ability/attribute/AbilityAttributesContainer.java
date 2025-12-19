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
import java.util.Map;
import java.util.Optional;

public record AbilityAttributesContainer(Map<RegistryEntry<AbilityAttribute>, Map<Identifier, AbilityAttributeModifier>> attributes) {
    public static final Codec<AbilityAttributesContainer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(JujutsuRegistries.ABILITY_ATTRIBUTE.getEntryCodec(), Codec.unboundedMap(Identifier.CODEC, AbilityAttributeModifier.CODEC))
                    .fieldOf("attributes").forGetter(AbilityAttributesContainer::attributes)
    ).apply(instance, AbilityAttributesContainer::new));

    public static final PacketCodec<RegistryByteBuf, AbilityAttributesContainer> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(HashMap::new, PacketCodecs.registryEntry(JujutsuRegistries.ABILITY_ATTRIBUTE_REGISTRY_KEY),
                    PacketCodecs.map(HashMap::new, Identifier.PACKET_CODEC, AbilityAttributeModifier.PACKET_CODEC)), AbilityAttributesContainer::attributes,
            AbilityAttributesContainer::new);

    public Map<Identifier, AbilityAttributeModifier> getModifiers(RegistryEntry<AbilityAttribute> attribute) {
        return attributes.get(attribute);
    }

    public void addModifier(RegistryEntry<AbilityAttribute> attribute, Identifier id, AbilityAttributeModifier modifier) {
        Map<Identifier, AbilityAttributeModifier> modifiers = getModifiers(attribute);
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
        private final Map<RegistryEntry<AbilityAttribute>, Map<Identifier, AbilityAttributeModifier>> map = new HashMap<>();

        public Builder addModifier(RegistryEntry<AbilityAttribute> attribute, Identifier id, double value, AbilityAttributeModifier.Type type) {
            Map<Identifier, AbilityAttributeModifier> modifiers = map.get(attribute);
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
            return addModifier(attribute, Jujutsu.id("base"), value, AbilityAttributeModifier.Type.ADD);
        }

        public AbilityAttributesContainer build() {
            return new AbilityAttributesContainer(map);
        }
    }
}
