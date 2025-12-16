package com.jujutsu.systems.buff;

import com.google.common.collect.ImmutableList;
import com.jujutsu.network.NbtPacketCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

import java.util.List;

public class Buff {
    public static final Codec<Buff> CODEC;
    public static final PacketCodec<RegistryByteBuf, Buff> PACKET_CODEC;

    private final List<BuffPredicate> conditions;
    private final CancellingPolicy cancellingPolicy;

    private Buff(List<BuffPredicate> conditions, CancellingPolicy cancellingPolicy) {
        this.conditions = conditions;
        this.cancellingPolicy = cancellingPolicy;
    }

    public boolean checkConditions(LivingEntity entity) {
        return switch (cancellingPolicy) {
            case ALL -> checkForAllPositiveConditions(entity);
            default -> checkForOnePositiveCondition(entity);
        };
    }

    private boolean checkForOnePositiveCondition(LivingEntity entity) {
        for(BuffPredicate condition: conditions) {
            if(condition.test(entity)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkForAllPositiveConditions(LivingEntity entity) {
        for(BuffPredicate condition: conditions) {
            if(!condition.test(entity)) {
                return false;
            }
        }
        return true;
    }

    public List<BuffPredicate> conditions() {
        return conditions;
    }

    public CancellingPolicy cancellingPolicy() {
        return cancellingPolicy;
    }

    public static void createBuff(LivingEntity entity, ImmutableList<BuffPredicate> conditions, CancellingPolicy cancellingPolicy, Identifier id) {
        if(hasBuff(entity, id)) return;

        BuffComponent component = BuffComponent.get(entity);
        Buff buff = new Buff(conditions, cancellingPolicy);

        component.addBuff(id, buff);
    }

    public static boolean hasBuff(LivingEntity entity, Identifier id) {
        BuffComponent component = BuffComponent.get(entity);
        return component.hasBuff(id);
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuffCancellingCondition.CODEC.listOf().fieldOf("conditions").forGetter(Buff::conditions),
                CancellingPolicy.CODEC.fieldOf("cancellingPolicy").forGetter(Buff::cancellingPolicy),
                IBuff.CODEC.fieldOf("buff").forGetter(Buff::buff)
                ).apply(instance, Buff::new)
        );

        PACKET_CODEC = new NbtPacketCodec<>(CODEC);
    }

    public enum CancellingPolicy {
        ALL(0),
        ONE_OR_MORE(1);

        private final int id;

        CancellingPolicy(int id) {
            this.id = id;
        }

        public static final Codec<CancellingPolicy> CODEC = Codec.INT.xmap(
                integer -> CancellingPolicy.values()[integer],
                cancellingPolicy -> cancellingPolicy.id
        );
    }
}
