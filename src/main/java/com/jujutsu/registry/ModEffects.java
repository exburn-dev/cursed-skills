package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.effect.IncinerationEffect;
import com.jujutsu.effect.StunEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ModEffects {
    public static final Holder<MobEffect> STUN = registerEffect("stun", new StunEffect(MobEffectCategory.HARMFUL, 0xFFFFFF)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, Jujutsu.id("stun"), -0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(ModAttributes.ROTATION_SPEED, Jujutsu.id("stun"), -0.85, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(ModAttributes.ROTATION_RESTRICTION, Jujutsu.id("stun"), -340, AttributeModifier.Operation.ADD_VALUE));

    public static final Holder<MobEffect> INCINERATION = registerEffect("incineration",
            new IncinerationEffect(MobEffectCategory.HARMFUL, 0xFF0000));

    private static Holder<MobEffect> registerEffect(String name, MobEffect effect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Jujutsu.id(name), effect);
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering status effects " + Jujutsu.MODID);
    }
}
