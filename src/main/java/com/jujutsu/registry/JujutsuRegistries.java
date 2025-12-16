package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.ability.core.AbilityType;
import com.jujutsu.systems.ability.attribute.AbilityAttribute;
import com.jujutsu.systems.ability.passive.PassiveAbilityType;
import com.jujutsu.systems.ability.upgrade.AbilityUpgradeRewardType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class JujutsuRegistries {
    public static final RegistryKey<Registry<AbilityType>> ABILITY_TYPE_REGISTRY_KEY = RegistryKey.ofRegistry(Jujutsu.id("ability"));
    public static final Registry<AbilityType> ABILITY_TYPE = FabricRegistryBuilder.createSimple(ABILITY_TYPE_REGISTRY_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static final RegistryKey<Registry<PassiveAbilityType<?>>> PASSIVE_ABILITY_TYPE_REGISTRY_KEY = RegistryKey.ofRegistry(Jujutsu.id("passive_ability"));
    public static final Registry<PassiveAbilityType<?>> PASSIVE_ABILITY_TYPE = FabricRegistryBuilder.createSimple(PASSIVE_ABILITY_TYPE_REGISTRY_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static final RegistryKey<Registry<BuffType<?>>> BUFF_TYPE_REGISTRY_KEY = RegistryKey.ofRegistry(Jujutsu.id("buff_type"));
    public static final Registry<BuffType<?>> BUFF_TYPE = FabricRegistryBuilder.createSimple(BUFF_TYPE_REGISTRY_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static final RegistryKey<Registry<BuffCancellingConditionType<?>>> BUFF_CANCELLING_CONDITION_KEY = RegistryKey.ofRegistry(Jujutsu.id("buff_cancelling_condition"));
    public static final Registry<BuffCancellingConditionType<?>> BUFF_CANCELLING_CONDITION_TYPE = FabricRegistryBuilder.createSimple(BUFF_CANCELLING_CONDITION_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static final RegistryKey<Registry<AbilityAttribute>> ABILITY_ATTRIBUTE_REGISTRY_KEY = RegistryKey.ofRegistry(Jujutsu.id("ability_attribute"));
    public static final Registry<AbilityAttribute> ABILITY_ATTRIBUTE = FabricRegistryBuilder.createSimple(ABILITY_ATTRIBUTE_REGISTRY_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static final RegistryKey<Registry<AbilityUpgradeRewardType<?>>> ABILITY_UPGRADE_REWARD_TYPE_REGISTRY_KEY = RegistryKey.ofRegistry(Jujutsu.id("ability_upgrade_type"));
    public static final Registry<AbilityUpgradeRewardType<?>> ABILITY_UPGRADE_REWARD_TYPE = FabricRegistryBuilder.createSimple(ABILITY_UPGRADE_REWARD_TYPE_REGISTRY_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();
}
