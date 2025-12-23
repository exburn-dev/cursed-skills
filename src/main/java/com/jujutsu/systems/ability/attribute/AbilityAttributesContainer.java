package com.jujutsu.systems.ability.attribute;

import com.jujutsu.registry.JujutsuRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public record AbilityAttributesContainer(Map<RegistryEntry<AbilityAttribute>, Map<Identifier, AbilityAttributeModifier>> attributes) {
    public static final Codec<AbilityAttributesContainer> CODEC = Codec.unboundedMap(
            JujutsuRegistries.ABILITY_ATTRIBUTE.getEntryCodec(),
            Codec.unboundedMap(Identifier.CODEC, AbilityAttributeModifier.CODEC))
            .xmap(AbilityAttributesContainer::new, AbilityAttributesContainer::attributes);

    public static final PacketCodec<RegistryByteBuf, AbilityAttributesContainer> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(HashMap::new, PacketCodecs.registryEntry(JujutsuRegistries.ABILITY_ATTRIBUTE_REGISTRY_KEY),
                    PacketCodecs.map(HashMap::new, Identifier.PACKET_CODEC, AbilityAttributeModifier.PACKET_CODEC)), AbilityAttributesContainer::attributes,
            AbilityAttributesContainer::new);

    public static AbilityAttributesContainer copyFrom(AbilityAttributesContainer container) {
        Map<RegistryEntry<AbilityAttribute>, Map<Identifier, AbilityAttributeModifier>> map = new HashMap<>();
        for(RegistryEntry<AbilityAttribute> attribute : container.attributes.keySet()) {
            Map<Identifier, AbilityAttributeModifier> modifiers = container.getModifiers(attribute);
            map.put(attribute, new HashMap<>(modifiers));
        }
        return new AbilityAttributesContainer(map);
    }

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
}
