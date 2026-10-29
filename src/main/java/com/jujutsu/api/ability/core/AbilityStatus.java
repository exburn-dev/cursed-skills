package com.jujutsu.api.ability.core;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum AbilityStatus {
    NONE(0),
    RUNNING(1),
    WAITING(2),
    ON_COOLDOWN(3),
    CANCELLED(4);

    public static final Codec<AbilityStatus> CODEC;
    public static final StreamCodec<FriendlyByteBuf, AbilityStatus> STREAM_CODEC;
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
        STREAM_CODEC = StreamCodec.of(
                (buf, status) -> buf.writeVarInt(status.getId()),
                (buf) -> AbilityStatus.fromId(buf.readVarInt()));
    }
}
