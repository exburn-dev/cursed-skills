package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.ability.upgrade.*;
import com.jujutsu.systems.ability.upgrade.reward.AbilityAttributeReward;
import com.jujutsu.systems.ability.upgrade.reward.EntityAttributeReward;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;

public class AbilityUpgradeRewardTypes {
    public static final AbilityUpgradeRewardType<AbilityAttributeReward> ABILITY_ATTRIBUTE = registerType("ability_attribute", AbilityAttributeReward.CODEC);
    public static final AbilityUpgradeRewardType<EntityAttributeReward> ENTITY_ATTRIBUTE = registerType("entity_attribute", EntityAttributeReward.CODEC);

    private static <T extends AbilityUpgradeReward> AbilityUpgradeRewardType<T> registerType(String name, MapCodec<T> codec) {
        return Registry.register(JujutsuRegistries.ABILITY_UPGRADE_REWARD_TYPE, Jujutsu.id(name), new AbilityUpgradeRewardType<>(codec));
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering ability upgrade types for " + Jujutsu.MODID);
    }
}
