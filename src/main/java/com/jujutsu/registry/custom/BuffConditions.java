package com.jujutsu.registry.custom;

import com.jujutsu.Jujutsu;
import com.jujutsu.api.buff.BuffCondition;
import com.jujutsu.api.buff.BuffConditionType;
import com.jujutsu.registry.JujutsuRegistries;
import com.jujutsu.api.buff.conditions.AbilityPropertyBuffCondition;
import com.jujutsu.api.buff.conditions.AttackBuffCondition;
import com.jujutsu.api.buff.conditions.TimerBuffCondition;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

public class BuffConditions {
    public static final BuffConditionType<TimerBuffCondition> TIMER = register("timer", TimerBuffCondition.CODEC);
    public static final BuffConditionType<AttackBuffCondition> ATTACK = register("attack", AttackBuffCondition.CODEC);
    public static final BuffConditionType<AbilityPropertyBuffCondition> ABILITY_PROPERTY = register("ability_property", AbilityPropertyBuffCondition.CODEC);

    private static <T extends BuffCondition> BuffConditionType<T> register(String name, Codec<T> codec) {
        return Registry.register(JujutsuRegistries.BUFF_PREDICATE_TYPE, Jujutsu.id(name), new BuffConditionType<>(codec));
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering buff predicates for " + Jujutsu.MODID);
    }
}
