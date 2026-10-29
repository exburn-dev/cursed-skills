package com.jujutsu.api.ability.core;

import com.jujutsu.registry.JujutsuRegistries;
import com.jujutsu.api.ability.attribute.AbilityAttribute;
import com.jujutsu.api.ability.attribute.AbilityAttributeComponent;
import com.jujutsu.api.ability.attribute.AbilityAttributesContainer;
import com.jujutsu.api.ability.data.ClientData;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public abstract class AbilityType {
    public static final Codec<AbilityType> CODEC = JujutsuRegistries.ABILITY_TYPE.byNameCodec();
    public static final Codec<Holder<AbilityType>> HOLDER_CODEC = JujutsuRegistries.ABILITY_TYPE.holderByNameCodec();
    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityType> STREAM_CODEC = ByteBufCodecs.registry(JujutsuRegistries.ABILITY_TYPE_KEY);

    private final int cooldownTime;
    private final boolean cancelable;
    private final ClientData clientData;

    public AbilityType(int cooldownTime, boolean cancelable) {
        this(cooldownTime, cancelable, null);
    }

    public AbilityType(int cooldownTime, boolean cancelable, ClientData clientData) {
        this.cooldownTime = cooldownTime;
        this.cancelable = cancelable;
        this.clientData = clientData;
    }

    public int getCooldownTime(Player player, AbilityInstance instance) {
        return this.cooldownTime;
    }

    public boolean isCancelable() {
        return this.cancelable;
    }

    public ClientData getClientData() {
        return this.clientData;
    }

    public abstract void start(Player player, AbilityInstance instance);
    public abstract void tick(Player player, AbilityInstance instance);
    public abstract void end(Player player, AbilityInstance instance);
    public abstract boolean isFinished(Player player, AbilityInstance instance);

    protected double getAttributeValue(Player player, Holder<AbilityAttribute> attribute) {
        return AbilityAttributeComponent.get(player).getAttributeValue(attribute);
    }

    public AbilityAttributesContainer getDefaultAttributes() {
        return AbilityAttributesContainer.builder().build();
    }

    public Style getStyle() {
        return Style.EMPTY;
    }

    public Component getName() {
        return Component.translatable(getTranslationKey());
    }

    public Component getDescription() {
        return Component.translatable(getTranslationKey().concat(".description"));
    }

    public String getTranslationKey() {
        Optional<ResourceKey<AbilityType>> optional = JujutsuRegistries.ABILITY_TYPE.getResourceKey(this);
        return optional.map(abilityTypeRegistryKey ->
                abilityTypeRegistryKey.identifier().toLanguageKey("ability")).orElse("");
    }
}
