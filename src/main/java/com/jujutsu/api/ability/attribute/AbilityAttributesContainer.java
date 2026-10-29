package com.jujutsu.api.ability.attribute;

import com.jujutsu.registry.JujutsuRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public record AbilityAttributesContainer(
        Map<Holder<AbilityAttribute>, AbilityAttributeModifier> base,
        Map<Holder<AbilityAttribute>, Map<Identifier, AbilityAttributeModifier>> modifiers) {

    public static final Codec<AbilityAttributesContainer> CODEC =
            RecordCodecBuilder.create((instance) -> instance.group(
                    Codec.unboundedMap(
                            JujutsuRegistries.ABILITY_ATTRIBUTE.holderByNameCodec(),
                            AbilityAttributeModifier.CODEC
                    ).fieldOf("base").forGetter(AbilityAttributesContainer::base),
                    Codec.unboundedMap(
                            JujutsuRegistries.ABILITY_ATTRIBUTE.holderByNameCodec(),
                            Codec.unboundedMap(Identifier.CODEC, AbilityAttributeModifier.CODEC)
                    ).fieldOf("modifiers").forGetter(AbilityAttributesContainer::modifiers)
            ).apply(instance, AbilityAttributesContainer::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityAttributesContainer> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new,
                    ByteBufCodecs.holderRegistry(JujutsuRegistries.ABILITY_ATTRIBUTE_KEY),
                    AbilityAttributeModifier.STREAM_CODEC
            ), AbilityAttributesContainer::base,
            ByteBufCodecs.map(HashMap::new,
                    ByteBufCodecs.holderRegistry(JujutsuRegistries.ABILITY_ATTRIBUTE_KEY),
                    ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, AbilityAttributeModifier.STREAM_CODEC)
            ), AbilityAttributesContainer::modifiers,
            AbilityAttributesContainer::new);

    public static AbilityAttributesContainer copyFrom(AbilityAttributesContainer container) {
        Map<Holder<AbilityAttribute>, AbilityAttributeModifier> base = new HashMap<>();
        Map<Holder<AbilityAttribute>, Map<Identifier, AbilityAttributeModifier>> modifiers = new HashMap<>();

        for(Holder<AbilityAttribute> attribute : container.base.keySet()) {
            base.put(attribute, container.getBaseModifier(attribute));
        }

        for(Holder<AbilityAttribute> attribute : container.modifiers.keySet()) {
            Map<Identifier, AbilityAttributeModifier> values = container.getModifiers(attribute);
            modifiers.put(attribute, new HashMap<>(values));
        }
        return new AbilityAttributesContainer(base, modifiers);
    }

    public Map<Identifier, AbilityAttributeModifier> getModifiers(Holder<AbilityAttribute> attribute) {
        return modifiers.get(attribute);
    }

    public AbilityAttributeModifier getBaseModifier(Holder<AbilityAttribute> attribute) {
        return base.get(attribute);
    }

    public void addBase(Holder<AbilityAttribute> attribute, double value) {
        base.put(attribute, new AbilityAttributeModifier(value, AbilityAttributeModifier.Type.ADD));
    }

    public void addModifier(Holder<AbilityAttribute> attribute, Identifier id, AbilityAttributeModifier modifier) {
        Map<Identifier, AbilityAttributeModifier> modifiers = getModifiers(attribute);
        if(modifiers != null) {
            modifiers.put(id, modifier);
            modifiers().put(attribute, modifiers);
        }
        else {
            HashMap<Identifier, AbilityAttributeModifier> map = new HashMap<>();
            map.put(id, modifier);
            modifiers().put(attribute, map);
        }
    }

    public static AbilityAttributesContainer emptyMutable() {
        return new AbilityAttributesContainer(new HashMap<>(), new HashMap<>());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<Holder<AbilityAttribute>, AbilityAttributeModifier> baseModifiers = new HashMap<>();
        private final Map<Holder<AbilityAttribute>, Map<Identifier, AbilityAttributeModifier>> modifiers = new HashMap<>();

        public Builder addBase(Holder<AbilityAttribute> attribute, double value) {
            baseModifiers.put(attribute, new AbilityAttributeModifier(value, AbilityAttributeModifier.Type.ADD));
            return this;
        }

        public Builder addModifier(Holder<AbilityAttribute> attribute, Identifier id, double value, AbilityAttributeModifier.Type type) {
            AbilityAttributeModifier modifier = new AbilityAttributeModifier(value, type);
            Map<Identifier, AbilityAttributeModifier> modifiersMap = modifiers.get(attribute);

            if(modifiersMap != null) {
                modifiersMap.put(id, modifier);
            }
            else {
                HashMap<Identifier, AbilityAttributeModifier> map = new HashMap<>();
                map.put(id, modifier);
                modifiers.put(attribute, map);
            }
            return this;
        }

        public AbilityAttributesContainer build() {
            return new AbilityAttributesContainer(baseModifiers, modifiers);
        }
    }
}
