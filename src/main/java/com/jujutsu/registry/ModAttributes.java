package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class ModAttributes {
    public static final Holder<Attribute> INVINCIBLE = registerRanged(
            "invincible", "attribute.jujutsu.invisible",
            0, 0, 1, false);
    public static final Holder<Attribute> FIRE_RESISTANCE = registerRanged(
            "fire_resistance", "attribute.jujutsu.fire_resistance",
            0, 0, 1, false);
    public static final Holder<Attribute> BLAST_RESISTANCE = registerRanged(
            "blast_resistance", "attribute.jujutsu.blast_resistance",
            0, 0, 1, false);
    public static final Holder<Attribute> ROTATION_RESTRICTION = registerRanged(
            "rotation_restriction", "attribute.jujutsu.rotation_restriction",
            360, 0, 360, true);
    public static final Holder<Attribute> ROTATION_SPEED = registerRanged("rotation_speed", "attribute.jujutsu.rotation_speed",
            1, 0, 2, true);
    public static final Holder<Attribute> JUMP_VELOCITY_MULTIPLIER = registerRanged(
            "jump_velocity_multiplier", "attribute.jujutsu.jump_velocity_multiplier",
            1, 0, 10, true);

    private static Holder<Attribute> registerRanged(String name, String translationKey,
                                                    double defaultValue, double min, double max, boolean isSynced) {
        return register(name, new RangedAttribute(translationKey, defaultValue, min, max).setSyncable(isSynced));
    }

    private static Holder<Attribute> register(String name, Attribute attribute) {
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, Jujutsu.id(name), attribute);
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering modifiers for " + Jujutsu.MODID);
    }
}
