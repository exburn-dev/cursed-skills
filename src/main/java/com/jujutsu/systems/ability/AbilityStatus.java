package com.jujutsu.systems.ability;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public enum AbilityStatus {
    NONE(0),
    RUNNING(1),
    WAITING(2),
    ON_COOLDOWN(3),
    CANCELLED(4);

    public static final Codec<AbilityStatus> CODEC;
    public static final PacketCodec<RegistryByteBuf, AbilityStatus> PACKET_CODEC;
    private final int id;

    AbilityStatus(int id) {
        this.id = id;
    }

    public static AbilityStatus fromId(int id) {
        return id < AbilityStatus.values().length ? AbilityStatus.values()[id] : NONE;
    }

    public int getId() {
        return id;
    }

    public  boolean isNone() {
        return this == NONE;
    }

    public boolean isRunning() {
        return this == RUNNING;
    }

    public boolean isWaiting() {
        return this == WAITING;
    }

    public boolean onCooldown() {
        return this == ON_COOLDOWN;
    }

    public boolean isCancelled() {
        return this == CANCELLED;
    }

    static {
        CODEC = Codec.INT.xmap(AbilityStatus::fromId, AbilityStatus::getId);
        PACKET_CODEC = PacketCodec.of(
                (status, buf) -> buf.writeVarInt(status.getId()),
                (buf) -> AbilityStatus.fromId(buf.readVarInt()));
    }
}
