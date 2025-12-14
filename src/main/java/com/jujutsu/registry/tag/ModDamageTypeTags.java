package com.jujutsu.registry.tag;

import com.jujutsu.Jujutsu;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModDamageTypeTags {
    public static final TagKey<DamageType> BYPASSES_INVINCIBILITY = of("bypasses_invincibility");

    private static TagKey<DamageType> of(String id) {
        return TagKey.of(RegistryKeys.DAMAGE_TYPE, Jujutsu.id(id));
    }
}
