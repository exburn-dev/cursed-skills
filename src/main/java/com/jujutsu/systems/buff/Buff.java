package com.jujutsu.systems.buff;

import com.google.common.collect.ImmutableList;
import com.jujutsu.network.NbtPacketCodec;
import com.jujutsu.systems.buff.type.AttributeBuff;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

import java.util.List;

public class Buff {
    private final LivingEntity entity;
    private final List<BuffPredicate> conditions;
    private final BuffProvider provider; //TODO: support of different buff providers
    private final boolean waitAllConditions;

    private Buff(LivingEntity entity, List<BuffPredicate> conditions, boolean waitAllConditions, BuffProvider provider) {
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
            provider.remove(entity, selfId);
            component().removeBuff(selfId);
        }
    }

    public boolean checkConditions() {
        for(BuffPredicate condition: conditions) {
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

    public List<BuffPredicate> conditions() {
        return conditions;
    }

    public BuffProvider provider() {
        return provider;
    }

    public static void createBuff(LivingEntity entity, AttributeBuff provider, ImmutableList<BuffPredicate> conditions, boolean waitAllConditions, Identifier id) {
        if(hasBuff(entity, id)) return;

        BuffComponent component = BuffComponent.get(entity);
        Buff buff = new Buff(entity, conditions, waitAllConditions, provider);

        component.addBuff(id, buff);
    }

    public static boolean hasBuff(LivingEntity entity, Identifier id) {
        BuffComponent component = BuffComponent.get(entity);
        return component.hasBuff(id);
    }
}
