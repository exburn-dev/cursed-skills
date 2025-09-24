package com.jujutsu.systems.buff;

import com.google.common.collect.ImmutableList;
import com.jujutsu.network.NbtPacketCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.List;

public class BuffWrapper {
    public static final Codec<BuffWrapper> CODEC;
    public static final PacketCodec<RegistryByteBuf, BuffWrapper> PACKET_CODEC;

    private final List<BuffCancellingCondition> conditions;
    private final CancellingPolicy cancellingPolicy;
    private final IBuff buff;

    private BuffWrapper(List<BuffCancellingCondition> conditions, CancellingPolicy cancellingPolicy, IBuff buff) {
        this.conditions = conditions;
        this.cancellingPolicy = cancellingPolicy;
        this.buff = buff;
    }

    public boolean checkConditions(LivingEntity entity) {
        return switch (cancellingPolicy) {
            case ALL -> checkForAllPositiveConditions(entity);
            default -> checkForOnePositiveCondition(entity);
        };
    }

    private boolean checkForOnePositiveCondition(LivingEntity entity) {
        for(BuffCancellingCondition condition: conditions) {
            if(condition.test(entity)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkForAllPositiveConditions(LivingEntity entity) {
        for(BuffCancellingCondition condition: conditions) {
            if(!condition.test(entity)) {
                return false;
            }
        }
        return true;
    }

    public List<BuffCancellingCondition> conditions() {
        return conditions;
    }

    public CancellingPolicy cancellingPolicy() {
        return cancellingPolicy;
    }

    public IBuff buff() {
        return buff;
    }

    public static void createBuff(LivingEntity entity, IBuff buff, ImmutableList<BuffCancellingCondition> conditions,
                                  CancellingPolicy cancellingPolicy, Identifier id) {
        if(hasBuff(entity, id)) return;

        BuffWrapper buffWrapper = new BuffWrapper(conditions, cancellingPolicy, buff);
        BuffHolder buffHolder = (BuffHolder) entity;

        buffHolder.addBuff(id, buffWrapper);
    }

    public static boolean hasBuff(LivingEntity entity, Identifier id) {
        BuffHolder buffHolder = (BuffHolder) entity;
        return buffHolder.getBuff(id) != null;
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuffCancellingCondition.CODEC.listOf().fieldOf("conditions").forGetter(BuffWrapper::conditions),
                CancellingPolicy.CODEC.fieldOf("cancellingPolicy").forGetter(BuffWrapper::cancellingPolicy),
                IBuff.CODEC.fieldOf("buff").forGetter(BuffWrapper::buff)
                ).apply(instance, BuffWrapper::new)
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
