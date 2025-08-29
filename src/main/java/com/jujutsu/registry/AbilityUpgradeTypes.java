package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.ability.upgrade.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;

public class AbilityUpgradeTypes {
    public static final AbilityUpgradeType<AbilityAttributeAbilityUpgrade> ABILITY_ATTRIBUTE = registerType("ability_attribute", AbilityAttributeAbilityUpgrade.CODEC);
    public static final AbilityUpgradeType<EntityAttributeAbilityUpgrade> ENTITY_ATTRIBUTE = registerType("entity_attribute", EntityAttributeAbilityUpgrade.CODEC);
    public static final AbilityUpgradeType<SequenceAbilityUpgrade> SEQUENCE = registerType("sequence", SequenceAbilityUpgrade.CODEC);

    private static <T extends AbilityUpgrade> AbilityUpgradeType<T> registerType(String name, MapCodec<T> codec) {
        return Registry.register(JujutsuRegistries.ABILITY_UPGRADE_TYPE, Jujutsu.getId(name), new AbilityUpgradeType<>(codec));
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering ability upgrade types for " + Jujutsu.MODID);
    }
}
