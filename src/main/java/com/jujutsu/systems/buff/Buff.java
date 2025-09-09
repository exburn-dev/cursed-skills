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
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.List;

public class Buff {
    public static final Codec<Buff> CODEC;
    public static final PacketCodec<RegistryByteBuf, Buff> PACKET_CODEC;

    private final RegistryEntry<EntityAttribute> attribute;
    private final List<BuffCancellingCondition> conditions;
    private final CancellingPolicy cancellingPolicy;

    private Buff(RegistryEntry<EntityAttribute> attribute, List<BuffCancellingCondition> conditions, CancellingPolicy cancellingPolicy) {
        this.attribute = attribute;
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

    public RegistryEntry<EntityAttribute> getAttribute() {
        return attribute;
    }

    public List<BuffCancellingCondition> getConditions() {
        return conditions;
    }

    public CancellingPolicy getCancellingPolicy() {
        return cancellingPolicy;
    }

    public static void createBuff(LivingEntity entity, RegistryEntry<EntityAttribute> attribute, ImmutableList<BuffCancellingCondition> conditions,
                                  CancellingPolicy cancellingPolicy, double value, EntityAttributeModifier.Operation operation, Identifier id) {
        if(hasBuff(entity, id) || entity.getAttributes().hasModifierForAttribute(attribute, id)) return;

        EntityAttributeModifier modifier = new EntityAttributeModifier(id, value, operation);
        Buff buff = new Buff(attribute, conditions, cancellingPolicy);
        BuffHolder buffHolder = (BuffHolder) entity;

        buffHolder.addBuff(id, buff, modifier);
    }

    public static boolean hasBuff(LivingEntity entity, Identifier id) {
        BuffHolder buffHolder = (BuffHolder) entity;
        return buffHolder.getBuff(id) != null;
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Registries.ATTRIBUTE.getEntryCodec().fieldOf("attribute").forGetter(Buff::getAttribute),
                BuffCancellingCondition.CODEC.listOf().fieldOf("conditions").forGetter(Buff::getConditions),
                CancellingPolicy.CODEC.fieldOf("cancellingPolicy").forGetter(Buff::getCancellingPolicy))
                .apply(instance, Buff::new)
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
