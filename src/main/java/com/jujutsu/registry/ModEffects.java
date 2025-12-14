package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.effect.IncinerationEffect;
import com.jujutsu.effect.StunEffect;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

public class ModEffects {
    public static final RegistryEntry<StatusEffect> STUN = registerEffect("stun", new StunEffect(StatusEffectCategory.HARMFUL, 0xFFFFFF)
            .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, Jujutsu.id("stun"), -0.25, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(ModAttributes.ROTATION_SPEED, Jujutsu.id("stun"), -0.85, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(ModAttributes.ROTATION_RESTRICTION, Jujutsu.id("stun"), -340, EntityAttributeModifier.Operation.ADD_VALUE));

    public static final RegistryEntry<StatusEffect> INCINERATION = registerEffect("incineration",
            new IncinerationEffect(StatusEffectCategory.HARMFUL, 0xFF0000));

    private static RegistryEntry<StatusEffect> registerEffect(String name, StatusEffect effect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Jujutsu.id(name), effect);
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering status effects " + Jujutsu.MODID);
    }
}
