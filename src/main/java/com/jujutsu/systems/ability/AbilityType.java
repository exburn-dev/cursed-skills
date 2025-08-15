package com.jujutsu.systems.ability;

import com.jujutsu.registry.JujutsuRegistries;
import com.jujutsu.systems.ability.attribute.AbilityAttribute;
import com.mojang.serialization.Codec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class AbilityType {
    public static final Codec<AbilityType> CODEC = JujutsuRegistries.ABILITY_TYPE.getCodec();
    public static final Codec<RegistryEntry<AbilityType>> ENTRY_CODEC = JujutsuRegistries.ABILITY_TYPE.getEntryCodec();
    private static final Codec<AbilityData> DATA_CODEC = Codec.unit(AbilityData.EMPTY);

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

    public int getCooldownTime(PlayerEntity player, AbilityInstance instance) {
        return this.cooldownTime;
    }

    public boolean isCancelable() {
        return this.cancelable;
    }

    public ClientData getClientData() {
        return this.clientData;
    }

    public abstract void start(PlayerEntity player, AbilityInstance instance);
    public abstract void tick(PlayerEntity player, AbilityInstance instance);
    public abstract void end(PlayerEntity player, AbilityInstance instance);
    public abstract boolean isFinished(PlayerEntity player, AbilityInstance instance);

    public AbilityData getInitialData() {
        return AbilityData.EMPTY;
    }

    public Codec<? extends AbilityData> getCodec() {
        return DATA_CODEC;
    }

    public Style getStyle() {
        return Style.EMPTY;
    }

    public Text getName() {
        return Text.translatable(getTranslationKey());
    }

    public Text getDescription() {
        return Text.translatable(getTranslationKey().concat(".description"));
    }

    public String getTranslationKey() {
        Optional<RegistryKey<AbilityType>> optional = JujutsuRegistries.ABILITY_TYPE.getKey(this);
        return optional.map(abilityTypeRegistryKey -> abilityTypeRegistryKey.getValue().toTranslationKey("ability")).orElse("");
    }

    public AbilityInstance getDefaultInstance() {
        return new AbilityInstance(this);
    }
}
