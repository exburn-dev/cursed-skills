package com.jujutsu.api.ability.passive;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jujutsu.registry.JujutsuRegistries;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public abstract class PassiveAbility {
    public static final Codec<PassiveAbility> CODEC = JujutsuRegistries.PASSIVE_ABILITY_TYPE.byNameCodec()
            .dispatch("type", PassiveAbility::getType, PassiveAbilityType::codec);
    public static final StreamCodec<ByteBuf, PassiveAbility> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public PassiveAbility() { }

    public void tick(Player player) { }

    public abstract void onGained(Player player);
    public abstract void onRemoved(Player player);

    @SafeVarargs
    protected final void addAttributes(Player player, Pair<Holder<Attribute>, AttributeModifier>... attributes) {
        Multimap<Holder<Attribute>, AttributeModifier> map = ArrayListMultimap.create();
        for(int i = 0; i < attributes.length; i++) {
            Pair<Holder<Attribute>, AttributeModifier> pair = attributes[i];

            map.put(pair.getFirst(), pair.getSecond());
        }
        player.getAttributes().addTransientAttributeModifiers(map);
    }

    protected final void addAttributes(Player player, Pair<Holder<Attribute>, AttributeModifier> attributes) {
        Multimap<Holder<Attribute>, AttributeModifier> map = ArrayListMultimap.create();
        map.put(attributes.getFirst(), attributes.getSecond());
        player.getAttributes().addTransientAttributeModifiers(map);
    }

    public Style getStyle() {
        return Style.EMPTY;
    }

    public Component getName() {
        return Component.translatable(getTranslationKey());
    }

    public String getTranslationKey() {
        Optional<ResourceKey<PassiveAbilityType<?>>> optional = JujutsuRegistries.PASSIVE_ABILITY_TYPE.getResourceKey(this.getType());
        return optional.map(key -> key.identifier().toLanguageKey("passive_ability")).orElse("");
    }

    public abstract PassiveAbilityType<?> getType();
}
