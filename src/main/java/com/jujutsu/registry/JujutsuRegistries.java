package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.api.ability.core.AbilitySlot;
import com.jujutsu.api.ability.core.AbilityType;
import com.jujutsu.api.ability.attribute.AbilityAttribute;
import com.jujutsu.api.ability.passive.PassiveAbilityType;
import com.jujutsu.api.ability.upgrade.AbilityUpgradeRewardType;
import com.jujutsu.api.buff.BuffConditionType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class JujutsuRegistries {
    public static final ResourceKey<Registry<AbilityType>> ABILITY_TYPE_KEY =
            ResourceKey.createRegistryKey(Jujutsu.id("ability"));
    public static final Registry<AbilityType> ABILITY_TYPE =
            FabricRegistryBuilder.createSimple(ABILITY_TYPE_KEY)
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();

    public static final ResourceKey<Registry<AbilitySlot>> ABILITY_SLOT_KEY =
            ResourceKey.createRegistryKey(Jujutsu.id("ability_slot"));
    public static final Registry<AbilitySlot> ABILITY_SLOT =
            FabricRegistryBuilder.createSimple(ABILITY_SLOT_KEY)
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();

    public static final ResourceKey<Registry<PassiveAbilityType<?>>> PASSIVE_ABILITY_TYPE_KEY =
            ResourceKey.createRegistryKey(Jujutsu.id("passive_ability"));
    public static final Registry<PassiveAbilityType<?>> PASSIVE_ABILITY_TYPE =
            FabricRegistryBuilder.createSimple(PASSIVE_ABILITY_TYPE_KEY)
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();

    public static final ResourceKey<Registry<BuffConditionType<?>>> BUFF_PREDICATE_KEY =
            ResourceKey.createRegistryKey(Jujutsu.id("buff_predicate"));
    public static final Registry<BuffConditionType<?>> BUFF_PREDICATE_TYPE =
            FabricRegistryBuilder.createSimple(BUFF_PREDICATE_KEY)
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();

    public static final ResourceKey<Registry<AbilityAttribute>> ABILITY_ATTRIBUTE_KEY =
            ResourceKey.createRegistryKey(Jujutsu.id("ability_attribute"));
    public static final Registry<AbilityAttribute> ABILITY_ATTRIBUTE =
            FabricRegistryBuilder.createSimple(ABILITY_ATTRIBUTE_KEY)
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();

    public static final ResourceKey<Registry<AbilityUpgradeRewardType<?>>> ABILITY_UPGRADE_REWARD_TYPE_KEY =
            ResourceKey.createRegistryKey(Jujutsu.id("ability_upgrade_type"));
    public static final Registry<AbilityUpgradeRewardType<?>> ABILITY_UPGRADE_REWARD_TYPE =
            FabricRegistryBuilder.createSimple(ABILITY_UPGRADE_REWARD_TYPE_KEY)
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();
}
