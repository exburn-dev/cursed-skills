package com.jujutsu.systems.buff;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.buff.conditions.AttackCancellingCondition;
import com.jujutsu.systems.buff.conditions.TimerBuffPredicate;
import net.minecraft.util.Identifier;

public class BuffPredicates {
    public static final BuffPredicateKey<TimerBuffPredicate> TIMER = new BuffPredicateKey<>(Jujutsu.id("timer"), TimerBuffPredicate.CODEC);
    public static final BuffPredicateKey<AttackCancellingCondition> ATTACK = new BuffPredicateKey<>(Jujutsu.id("attack"), AttackCancellingCondition.CODEC);

    @SuppressWarnings("unchecked")
    public static  <T extends BuffPredicate> BuffPredicateKey<T> byId(Identifier id) {
        if(id.equals(TIMER.id())) {
            return (BuffPredicateKey<T>) TIMER;
        }
        return null;
    }
}
