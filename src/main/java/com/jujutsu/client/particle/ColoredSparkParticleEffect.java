package com.jujutsu.client.particle;

import com.jujutsu.registry.ModParticleTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Vector3f;

public class ColoredSparkParticleEffect implements IVelocityParticleEffect {
    public static final MapCodec<ColoredSparkParticleEffect> CODEC;
    public static final PacketCodec<RegistryByteBuf, ColoredSparkParticleEffect> PACKET_CODEC;

    private Vector3f velocity = new Vector3f(0, 0, 0);
    private final float startScale;
    private final float scaleModifier;
    private final ColorTransition color;
    private final RollData roll;
    private final int lifetime;

    public ColoredSparkParticleEffect(float startScale, float scaleModifier, ColorTransition color, float startRoll, float roll, int lifetime) {
        this.startScale = startScale;
        this.scaleModifier = scaleModifier;
        this.color = color;
        this.roll = new RollData(startRoll, roll);
        this.lifetime = lifetime;
    }

    public ColoredSparkParticleEffect(Vector3f velocity, float startScale, float scaleModifier, ColorTransition color, RollData roll, int lifetime) {
        this.velocity = velocity;
        this.startScale = startScale;
        this.scaleModifier = scaleModifier;
        this.color = color;
        this.roll = roll;
        this.lifetime = lifetime;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticleTypes.COLORED_SPARK;
    }

    public float getScaleModifier() {
        return scaleModifier;
    }

    public float getStartScale() {
        return startScale;
    }

    public ColorTransition getColor() {
        return color;
    }

    public RollData getRoll() {
        return roll;
    }

    public int getLifetime() {
        return lifetime;
    }

    public record ColorTransition(Vector3f startColor, Vector3f endColor) {
        public static final Codec<ColorTransition> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(Codecs.VECTOR_3F.fieldOf("startColor").forGetter(ColorTransition::startColor), Codecs.VECTOR_3F.fieldOf("endColor").forGetter(ColorTransition::endColor)).apply(instance, ColorTransition::new));
        public static final PacketCodec<RegistryByteBuf, ColorTransition> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.VECTOR3F, ColorTransition::startColor,  PacketCodecs.VECTOR3F, ColorTransition::endColor, ColorTransition::new);
    }

    public record RollData(float startRoll, float roll) {
        public static final Codec<RollData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(Codec.FLOAT.fieldOf("startRoll").forGetter(RollData::startRoll), Codec.FLOAT.fieldOf("roll").forGetter(RollData::roll)).apply(instance, RollData::new));
        public static final PacketCodec<RegistryByteBuf, RollData> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, RollData::startRoll, PacketCodecs.FLOAT, RollData::roll, RollData::new);
    }

    static {
        CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codecs.VECTOR_3F.fieldOf("velocity").forGetter(ColoredSparkParticleEffect::getVelocity),
                Codecs.POSITIVE_FLOAT.fieldOf("startScale").forGetter(ColoredSparkParticleEffect::getStartScale),
                Codecs.POSITIVE_FLOAT.fieldOf("scaleModifier").forGetter(ColoredSparkParticleEffect::getScaleModifier),
                ColorTransition.CODEC.fieldOf("color").forGetter(ColoredSparkParticleEffect::getColor),
                RollData.CODEC.fieldOf("roll").forGetter(ColoredSparkParticleEffect::getRoll),
                Codecs.POSITIVE_INT.fieldOf("lifetime").forGetter(ColoredSparkParticleEffect::getLifetime)
        ).apply(instance, ColoredSparkParticleEffect::new));

        PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.VECTOR3F, ColoredSparkParticleEffect::getVelocity,
                PacketCodecs.FLOAT, ColoredSparkParticleEffect::getStartScale,
                PacketCodecs.FLOAT, ColoredSparkParticleEffect::getScaleModifier,
                ColorTransition.PACKET_CODEC, ColoredSparkParticleEffect::getColor,
                RollData.PACKET_CODEC, ColoredSparkParticleEffect::getRoll,
                PacketCodecs.INTEGER, ColoredSparkParticleEffect::getLifetime,
                ColoredSparkParticleEffect::new
        );
    }
}
