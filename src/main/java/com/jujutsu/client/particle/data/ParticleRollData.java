package com.jujutsu.client.particle.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ParticleRollData(float startRoll, float roll) {
    public static final Codec<ParticleRollData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("startRoll").forGetter(ParticleRollData::startRoll),
            Codec.FLOAT.fieldOf("roll").forGetter(ParticleRollData::roll)
    ).apply(instance, ParticleRollData::new));

    public static final StreamCodec<FriendlyByteBuf, ParticleRollData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, ParticleRollData::startRoll,
            ByteBufCodecs.FLOAT, ParticleRollData::roll,
            ParticleRollData::new);
}
