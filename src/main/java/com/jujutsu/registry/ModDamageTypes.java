package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> HOLLOW_PURPLE = ResourceKey.create(Registries.DAMAGE_TYPE, Jujutsu.id("hollow_purple"));
}
