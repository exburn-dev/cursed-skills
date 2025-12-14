package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

public class ModAttributes {
    public static final RegistryEntry<EntityAttribute> INVINCIBLE = registerAttribute("invincible", new ClampedEntityAttribute("attribute.jujutsu.invisible", 0, 0, 1));
    public static final RegistryEntry<EntityAttribute> FIRE_RESISTANCE = registerAttribute("fire_resistance", new ClampedEntityAttribute("attribute.jujutsu.fire_resistance", 0, 0, 1));
    public static final RegistryEntry<EntityAttribute> BLAST_RESISTANCE = registerAttribute("blast_resistance", new ClampedEntityAttribute("attribute.jujutsu.blast_resistance", 0, 0, 1));
    public static final RegistryEntry<EntityAttribute> ROTATION_RESTRICTION = registerAttribute("rotation_restriction", new ClampedEntityAttribute("attribute.jujutsu.rotation_restriction", 360, 0, 360).setTracked(true));
    public static final RegistryEntry<EntityAttribute> ROTATION_SPEED = registerAttribute("rotation_speed", new ClampedEntityAttribute("attribute.jujutsu.rotation_speed", 1, 0, 2).setTracked(true));
    public static final RegistryEntry<EntityAttribute> JUMP_VELOCITY_MULTIPLIER = registerAttribute("jump_velocity_multiplier", new ClampedEntityAttribute("attribute.jujutsu.jump_velocity_multiplier", 1, 0, 10).setTracked(true));

    private static RegistryEntry<EntityAttribute> registerAttribute(String name, EntityAttribute attribute) {
        return Registry.registerReference(Registries.ATTRIBUTE, Jujutsu.id(name), attribute);
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering attributes for " + Jujutsu.MODID);
    }
}
