package com.jujutsu.api.buff;

import com.google.common.collect.ImmutableList;
import com.jujutsu.api.buff.type.AttributeBuff;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class Buff {
    private final LivingEntity entity;
    private final List<BuffCondition> conditions;
    private final BuffProvider provider;
    private final boolean waitAllConditions;

    private Buff(LivingEntity entity, List<BuffCondition> conditions, boolean waitAllConditions, BuffProvider provider) {
        this.entity = entity;
        this.conditions = conditions;
        this.waitAllConditions = waitAllConditions;
        this.provider = provider;
    }

    public Buff(LivingEntity entity, BuffData data) {
        this(entity, data.conditions(), data.waitAllConditions(), data.provider());
    }

    public void tick(Identifier selfId) {
        if(checkConditions()) {
            component().markForRemoval(selfId);
        }
    }

    public boolean checkConditions() {
        for(BuffCondition condition: conditions) {
            boolean conditionResult = condition.test(entity);

            if(!waitAllConditions && conditionResult) {
                return true;
            }
            else if(waitAllConditions && !conditionResult) {
                return  false;
            }
        }
        return waitAllConditions;
    }

    public BuffData getData() {
        return new BuffData(conditions, waitAllConditions, (AttributeBuff) provider);
    }

    private BuffComponent component() {
        return BuffComponent.get(entity);
    }

    public List<BuffCondition> conditions() {
        return conditions;
    }

    public BuffProvider provider() {
        return provider;
    }

    public static void createBuff(LivingEntity entity, AttributeBuff provider, ImmutableList<BuffCondition> conditions, boolean waitAllConditions, Identifier id) {
        if(hasBuff(entity, id)) return;

        BuffComponent component = BuffComponent.get(entity);
        Buff buff = new Buff(entity, conditions, waitAllConditions, provider);

        provider.apply(entity, id);
        component.addBuff(id, buff);
    }

    public static boolean hasBuff(LivingEntity entity, Identifier id) {
        BuffComponent component = BuffComponent.get(entity);
        return component.hasBuff(id);
    }
}
