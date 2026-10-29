package com.jujutsu.registry.custom;

import com.jujutsu.Jujutsu;
import com.jujutsu.api.ability.attribute.AbilityAttribute;
import com.jujutsu.registry.JujutsuRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;

public class AbilityAttributes {
    public static final Holder<AbilityAttribute> HOLLOW_PURPLE_RADIUS = registerMeters("hollow_purple.radius");
    public static final Holder<AbilityAttribute> HOLLOW_PURPLE_SPEED = registerNumber("hollow_purple.speed");

    public static final Holder<AbilityAttribute> INFINITY_DURATION = registerSeconds("infinity.duration");

    public static final Holder<AbilityAttribute> REVERSAL_RED_EXPLOSION_POWER = registerNumber("reversal_red.explosion_power");
    public static final Holder<AbilityAttribute> REVERSAL_RED_DAMAGE_MULTIPLIER = registerNumber("reversal_red.damage_multiplier");
    public static final Holder<AbilityAttribute> REVERSAL_RED_STUN = registerSeconds("reversal_red.stun");
    public static final Holder<AbilityAttribute> REVERSAL_RED_CHARGE_TIME = registerSeconds("reversal_red.charge_time");

    public static final Holder<AbilityAttribute> LAPSE_BLUE_DAMAGE_MULTIPLIER = registerNumber("lapse_blue.damage_multiplier");
    public static final Holder<AbilityAttribute> LAPSE_BLUE_STUN = registerSeconds("lapse_blue.stun");

    public static final Holder<AbilityAttribute> SONIC_RIFT_DASH_POWER = registerNumber("sonic_riftl.dash_power");
    public static final Holder<AbilityAttribute> SONIC_RIFT_START_JUMP_POWER = registerNumber("sonic_rift.start_jump_power");
    public static final Holder<AbilityAttribute> SONIC_RIFT_ADDITIONAL_DASHES = registerNumber("sonic_rift.additional_dashes");
    public static final Holder<AbilityAttribute> SONIC_RIFT_DAMAGE = registerNumber("sonic_rift.damage");

    public static final Holder<AbilityAttribute> SUPERSONIC_SPEED = registerNumber("supersonic.speed");
    public static final Holder<AbilityAttribute> SUPERSONIC_DURATION = registerSeconds("supersonic.duration");

    private static Holder<AbilityAttribute> registerNumber(String name) {
        return register(name, AbilityAttribute.MeasureUnit.NUMBER);
    }

    private static Holder<AbilityAttribute> registerSeconds(String name) {
        return register(name, AbilityAttribute.MeasureUnit.SECONDS);
    }

    private static Holder<AbilityAttribute> registerMeters(String name) {
        return register(name, AbilityAttribute.MeasureUnit.METERS);
    }

    private static Holder<AbilityAttribute> register(String name, AbilityAttribute.MeasureUnit unit) {
        return Registry.registerForHolder(JujutsuRegistries.ABILITY_ATTRIBUTE, Jujutsu.id(name), new AbilityAttribute(unit));
    }


    public static void register() {
        Jujutsu.LOGGER.info("Registering ability modifiers for " + Jujutsu.MODID);
    }
}
