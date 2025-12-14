package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.ability.attribute.AbilityAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

import static com.jujutsu.systems.ability.attribute.AbilityAttribute.MeasureUnit;

public class ModAbilityAttributes {
    public static final RegistryEntry<AbilityAttribute> HOLLOW_PURPLE_RADIUS = registerAttribute("hollow_purple_radius", MeasureUnit.METERS);
    public static final RegistryEntry<AbilityAttribute> HOLLOW_PURPLE_SPEED = registerAttribute("hollow_purple_speed", MeasureUnit.NUMBER);

    public static final RegistryEntry<AbilityAttribute> INFINITY_DURATION = registerAttribute("infinity_duration", MeasureUnit.SECONDS);

    public static final RegistryEntry<AbilityAttribute> REVERSAL_RED_EXPLOSION_POWER = registerAttribute("reversal_red_explosion_power", MeasureUnit.NUMBER);
    public static final RegistryEntry<AbilityAttribute> REVERSAL_RED_DAMAGE_MULTIPLIER = registerAttribute("reversal_red_damage_multiplier", MeasureUnit.NUMBER);
    public static final RegistryEntry<AbilityAttribute> REVERSAL_RED_STUN = registerAttribute("reversal_red_stun", MeasureUnit.SECONDS);
    public static final RegistryEntry<AbilityAttribute> REVERSAL_RED_CHARGE_TIME = registerAttribute("reversal_red_charge_time", MeasureUnit.SECONDS);

    public static final RegistryEntry<AbilityAttribute> LAPSE_BLUE_DAMAGE_MULTIPLIER = registerAttribute("lapse_blue_damage_multiplier", MeasureUnit.NUMBER);
    public static final RegistryEntry<AbilityAttribute> LAPSE_BLUE_STUN = registerAttribute("lapse_blue_stun", MeasureUnit.SECONDS);

    public static final RegistryEntry<AbilityAttribute> SONIC_RIFT_DASH_POWER = registerAttribute("sonic_rift_dash_power", MeasureUnit.NUMBER);
    public static final RegistryEntry<AbilityAttribute> SONIC_RIFT_START_JUMP_POWER = registerAttribute("sonic_rift_start_jump_power", MeasureUnit.NUMBER);
    public static final RegistryEntry<AbilityAttribute> SONIC_RIFT_ADDITIONAL_DASHES = registerAttribute("sonic_rift_additional_dashes", MeasureUnit.NUMBER);
    public static final RegistryEntry<AbilityAttribute> SONIC_RIFT_DAMAGE = registerAttribute("sonic_rift_additional_dashes", MeasureUnit.NUMBER);

    public static final RegistryEntry<AbilityAttribute> SUPERSONIC_SPEED = registerAttribute("supersonic_speed", MeasureUnit.NUMBER);
    public static final RegistryEntry<AbilityAttribute> SUPERSONIC_DURATION = registerAttribute("supersonic_duration", MeasureUnit.SECONDS);

    private static RegistryEntry<AbilityAttribute> registerAttribute(String name, MeasureUnit unit) {
        return Registry.registerReference(JujutsuRegistries.ABILITY_ATTRIBUTE, Jujutsu.id(name), new AbilityAttribute(unit));
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering ability attributes for " + Jujutsu.MODID);
    }
}
