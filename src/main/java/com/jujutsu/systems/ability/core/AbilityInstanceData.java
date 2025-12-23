package com.jujutsu.systems.ability.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.HashMap;
import java.util.Map;

public class AbilityInstanceData {
    public static final Codec<AbilityInstanceData> CODEC;
    public static final Codec<Map<AbilitySlot, AbilityInstanceData>> MAP_CODEC;
    public static final PacketCodec<RegistryByteBuf, AbilityInstanceData> PACKET_CODEC;

    private final AbilityType type;
    private final AbilitySlot slot;
    private AbilityStatus status;
    private int useTime;
    private int cooldownTime;

    public AbilityInstanceData(AbilityType type, AbilitySlot slot, AbilityStatus status, int useTime, int cooldownTime) {
        this.type = type;
        this.slot = slot;
        this.status = status;
        this.useTime = useTime;
        this.cooldownTime = cooldownTime;
    }

    public AbilityType type() {
        return type;
    }
    public AbilitySlot slot() {
        return slot;
    }
    public AbilityStatus status() {
        return status;
    }
    public int useTime() {
        return useTime;
    }
    public int cooldownTime() {
        return cooldownTime;
    }

    public void setCooldownTime(int value) {
        cooldownTime = value;
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                AbilityType.CODEC.fieldOf("type").forGetter(AbilityInstanceData::type),
                AbilitySlot.CODEC.fieldOf("slot").forGetter(AbilityInstanceData::slot),
                AbilityStatus.CODEC.fieldOf("status").forGetter(AbilityInstanceData::status),
                Codec.INT.fieldOf("useTime").forGetter(AbilityInstanceData::useTime),
                Codec.INT.fieldOf("cooldownTime").forGetter(AbilityInstanceData::cooldownTime)
        ).apply(instance, AbilityInstanceData::new));

        PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.registryCodec(AbilityType.CODEC), AbilityInstanceData::type,
                AbilitySlot.PACKET_CODEC, AbilityInstanceData::slot,
                AbilityStatus.PACKET_CODEC, AbilityInstanceData::status,
                PacketCodecs.INTEGER, AbilityInstanceData::useTime,
                PacketCodecs.INTEGER, AbilityInstanceData::cooldownTime,
                AbilityInstanceData::new
        );

        MAP_CODEC = CODEC.listOf().xmap(
                list -> {
                    Map<AbilitySlot, AbilityInstanceData> map = new HashMap<>();
                    list.forEach(data -> map.put(data.slot(), data));
                    return ImmutableMap.copyOf(map);
                },
                map -> map.values().stream().toList()
        );
    }
}
