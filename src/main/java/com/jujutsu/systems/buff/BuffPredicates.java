package com.jujutsu.systems.buff;

import com.jujutsu.Jujutsu;
import com.jujutsu.registry.JujutsuRegistries;
import com.jujutsu.systems.buff.conditions.AttackBuffPredicate;
import com.jujutsu.systems.buff.conditions.TimerBuffPredicate;
import com.mojang.serialization.Codec;
import net.minecraft.registry.Registry;

public class BuffPredicates {
    public static final BuffPredicateType<TimerBuffPredicate> TIMER = register("timer", TimerBuffPredicate.CODEC);
    public static final BuffPredicateType<AttackBuffPredicate> ATTACK = register("attack", AttackBuffPredicate.CODEC);

    private static <T extends BuffPredicate> BuffPredicateType<T> register(String name, Codec<T> codec) {
        return Registry.register(JujutsuRegistries.BUFF_PREDICATE_TYPE, Jujutsu.id(name), new BuffPredicateType<>(codec));
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering buff predicates for " + Jujutsu.MODID);
    }
}
