package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.buff.BuffCancellingCondition;
import com.jujutsu.systems.buff.BuffCancellingConditionType;
import com.jujutsu.systems.buff.BuffType;
import com.jujutsu.systems.buff.IBuff;
import com.jujutsu.systems.buff.conditions.AttackCancellingCondition;
import com.jujutsu.systems.buff.conditions.TimeCancellingCondition;
import com.jujutsu.systems.buff.type.ConstantBuff;
import com.jujutsu.systems.buff.type.SupersonicBuff;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.registry.Registry;

public class BuffTypes {
    public static final BuffType<ConstantBuff> CONSTANT_BUFF_TYPE = registerBuff("constant_buff", ConstantBuff.CODEC);
    public static final BuffType<SupersonicBuff> SUPERSONIC_BUFF_TYPE = registerBuff("supersonic_buff", SupersonicBuff.CODEC);

    public static final BuffCancellingConditionType<TimeCancellingCondition> TIME_CANCELLING_CONDITION = registerCancellingCondition("time_cancelling_condition", new BuffCancellingConditionType<>(TimeCancellingCondition.CODEC));
    public static final BuffCancellingConditionType<AttackCancellingCondition> ATTACK_CANCELLING_CONDITION = registerCancellingCondition("attack_cancelling_condition", new BuffCancellingConditionType<>(AttackCancellingCondition.CODEC));

    private static <T extends IBuff> BuffType<T> registerBuff(String name, MapCodec<T> codec) {
        return Registry.register(JujutsuRegistries.BUFF_TYPE, Jujutsu.id(name), new BuffType<>(codec));
    }

    private static <T extends IBuff, E> BuffType<T> registerDynamicBuff(String name, MapCodec<T> codec, Event<E> subscribeTo, E callback) {
        subscribeTo.register(callback);

        return Registry.register(JujutsuRegistries.BUFF_TYPE, Jujutsu.id(name), new BuffType<>(codec));
    }

    private static <T extends BuffCancellingCondition> BuffCancellingConditionType<T> registerCancellingCondition(String id, BuffCancellingConditionType<T> type) {
        return Registry.register(JujutsuRegistries.BUFF_CANCELLING_CONDITION_TYPE, Jujutsu.id(id), type);
    }

    public static void registerCancellingCondition() {
        Jujutsu.LOGGER.info("Registering buff cancelling condition types for " + Jujutsu.MODID);
    }
}
