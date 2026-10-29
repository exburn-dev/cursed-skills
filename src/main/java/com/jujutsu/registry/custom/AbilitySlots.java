package com.jujutsu.registry.custom;

import com.jujutsu.Jujutsu;
import com.jujutsu.api.ability.core.AbilitySlot;
import com.jujutsu.registry.JujutsuRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class AbilitySlots {
    public static final AbilitySlot SLOT_1 = registerManual("slot_1");
    public static final AbilitySlot SLOT_2 = registerManual("slot_2");
    public static final AbilitySlot SLOT_3 = registerManual("slot_3");
    public static final AbilitySlot SLOT_4 = registerManual("slot_4");
    public static final AbilitySlot SLOT_ON_DEATH = registerTriggered("slot_on_death");

    private static AbilitySlot registerManual(String name) {
        return register(name, AbilitySlot.Activation.MANUAL);
    }

    private static AbilitySlot registerTriggered(String name) {
        return register(name, AbilitySlot.Activation.TRIGGERED);
    }

    private static AbilitySlot register(String name, AbilitySlot.Activation activationType) {
        ResourceKey<AbilitySlot> key = ResourceKey.create(JujutsuRegistries.ABILITY_SLOT_KEY, Jujutsu.id(name));
        return Registry.register(JujutsuRegistries.ABILITY_SLOT, key, new AbilitySlot(key, activationType));
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registring ability slots for " + Jujutsu.MODID);
    }
}
