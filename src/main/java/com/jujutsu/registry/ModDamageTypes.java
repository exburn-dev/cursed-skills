package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class ModDamageTypes {
    public static final RegistryKey<DamageType> HOLLOW_PURPLE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Jujutsu.id("hollow_purple"));
}
