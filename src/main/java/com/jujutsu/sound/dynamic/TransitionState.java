package com.jujutsu.sound.dynamic;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.Objects;

public enum TransitionState implements StringRepresentable {
    STARTING("starting"),
    RUNNING("running"),
    ENDING("ending");

    public static final Codec<TransitionState> CODEC;
    public static final StreamCodec<ByteBuf, TransitionState> STREAM_CODEC;
    private final String id;

    TransitionState(String id) {
        this.id = id;
    }

    public static TransitionState fromString(String id) {
        if(Objects.equals(id, RUNNING.id)) {
            return RUNNING;
        }
        else if(Objects.equals(id, ENDING.id)) {
            return ENDING;
        }
        return STARTING;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }

    static {
        CODEC = StringRepresentable.fromEnum(TransitionState::values);
        STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(TransitionState::fromString, TransitionState::getSerializedName);
    }
}
