package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.ability.attribute.AbilityAttribute;
import net.minecraft.registry.Registry;

public class ModAbilityAttributes {


    private static <T extends AbilityAttribute> T registerAttribute(String name, T attribute) {
        return Registry.register(JujutsuRegistries.ABILITY_ATTRIBUTE, Jujutsu.getId(name), attribute);
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering ability attributes for " + Jujutsu.MODID);
    }
}
