package com.jujutsu.client.particle.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record ParticleColorTransition(Vector3fc startColor, Vector3fc endColor) {
    public static final Codec<ParticleColorTransition> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ExtraCodecs.VECTOR3F.fieldOf("startColor").forGetter(ParticleColorTransition::startColor),
                    ExtraCodecs.VECTOR3F.fieldOf("endColor").forGetter(ParticleColorTransition::endColor)
            ).apply(instance, ParticleColorTransition::new));

    public static final StreamCodec<FriendlyByteBuf, ParticleColorTransition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F, ParticleColorTransition::startColor,
            ByteBufCodecs.VECTOR3F, ParticleColorTransition::endColor,
            ParticleColorTransition::new);

    public static ParticleColorTransition ofStaticColor(float r, float g, float b) {
        return of(r, g, b, r, g, b);
    }

    public static ParticleColorTransition of(float r, float g, float b, float r1, float g1, float b1) {
        return new ParticleColorTransition(
                new Vector3f(r, g, b),
                new Vector3f(r1, g1, b1)
        );
    }
}
