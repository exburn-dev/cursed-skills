package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.buff.BuffCancellingCondition;
import com.jujutsu.systems.buff.BuffCancellingConditionType;
import com.jujutsu.systems.buff.conditions.AttackCancellingCondition;
import com.jujutsu.systems.buff.conditions.TimeCancellingCondition;
import net.minecraft.registry.Registry;

public class BuffCancellingConditionTypes {
    public static final BuffCancellingConditionType<TimeCancellingCondition> TIME_CANCELLING_CONDITION = register("time_cancelling_condition", new BuffCancellingConditionType<>(TimeCancellingCondition.CODEC));
    public static final BuffCancellingConditionType<AttackCancellingCondition> ATTACK_CANCELLING_CONDITION = register("attack_cancelling_condition", new BuffCancellingConditionType<>(AttackCancellingCondition.CODEC));

    private static <T extends BuffCancellingCondition>BuffCancellingConditionType<T> register(String id, BuffCancellingConditionType<T> type) {
        return Registry.register(JujutsuRegistries.BUFF_CANCELLING_CONDITION_TYPE, Jujutsu.getId(id), type);
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering buff cancelling condition types for " + Jujutsu.MODID);
    }
}
