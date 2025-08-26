package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.ability.attribute.AbilityAttribute;
import net.minecraft.registry.Registry;

public class ModAbilityAttributes {
    public static final AbilityAttribute HOLLOW_PURPLE_RADIUS = registerAttribute("hollow_purple_radius");

    private static AbilityAttribute registerAttribute(String name) {
        return Registry.register(JujutsuRegistries.ABILITY_ATTRIBUTE, Jujutsu.getId(name), new AbilityAttribute());
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering ability attributes for " + Jujutsu.MODID);
    }
}
