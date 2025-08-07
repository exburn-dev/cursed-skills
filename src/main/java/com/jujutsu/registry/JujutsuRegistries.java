package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.ability.AbilityType;
import com.jujutsu.systems.ability.PassiveAbility;
import com.jujutsu.systems.ability.PassiveAbilityType;
import com.jujutsu.systems.buff.BuffCancellingConditionType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class JujutsuRegistries {
    public static final RegistryKey<Registry<AbilityType>> ABILITY_TYPE_REGISTRY_KEY = RegistryKey.ofRegistry(Jujutsu.getId("ability"));
    public static final Registry<AbilityType> ABILITY_TYPE = FabricRegistryBuilder.createSimple(ABILITY_TYPE_REGISTRY_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static final RegistryKey<Registry<PassiveAbilityType<?>>> PASSIVE_ABILITY_TYPE_REGISTRY_KEY = RegistryKey.ofRegistry(Jujutsu.getId("passive_ability"));
    public static final Registry<PassiveAbilityType<?>> PASSIVE_ABILITY_TYPE = FabricRegistryBuilder.createSimple(PASSIVE_ABILITY_TYPE_REGISTRY_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static final RegistryKey<Registry<BuffCancellingConditionType<?>>> BUFF_CANCELLING_CONDITION_KEY = RegistryKey.ofRegistry(Jujutsu.getId("buff_cancelling_condition"));
    public static final Registry<BuffCancellingConditionType<?>> BUFF_CANCELLING_CONDITION_TYPE = FabricRegistryBuilder.createSimple(BUFF_CANCELLING_CONDITION_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();
}
