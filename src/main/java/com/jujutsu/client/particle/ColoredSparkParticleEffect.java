package com.jujutsu.client.particle;

import com.jujutsu.client.particle.data.ParticleColorTransition;
import com.jujutsu.client.particle.data.ParticleRollData;
import com.jujutsu.registry.ModParticleTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public class ColoredSparkParticleEffect implements IVelocityParticleEffect {
    public static final MapCodec<ColoredSparkParticleEffect> CODEC;
    public static final StreamCodec<FriendlyByteBuf, ColoredSparkParticleEffect> STREAM_CODEC;

    private Vec3 velocity = Vec3.ZERO;
    private final float startScale;
    private final float scaleModifier;
    private final ParticleColorTransition color;
    private final ParticleRollData roll;
    private final int lifetime;

    public ColoredSparkParticleEffect(float startScale, float scaleModifier, ParticleColorTransition color, float startRoll, float roll, int lifetime) {
        this.startScale = startScale;
        this.scaleModifier = scaleModifier;
        this.color = color;
        this.roll = new ParticleRollData(startRoll, roll);
        this.lifetime = lifetime;
    }

    public ColoredSparkParticleEffect(Vec3 velocity, float startScale, float scaleModifier, ParticleColorTransition color, ParticleRollData roll, int lifetime) {
        this.velocity = velocity;
        this.startScale = startScale;
        this.scaleModifier = scaleModifier;
        this.color = color;
        this.roll = roll;
        this.lifetime = lifetime;
    }

    public void setVelocity(Vec3 velocity) {
        this.velocity = velocity;
    }

    public Vec3 getVelocity() {
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

    public ParticleColorTransition getColor() {
        return color;
    }

    public ParticleRollData getRoll() {
        return roll;
    }

    public int getLifetime() {
        return lifetime;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private Vec3 velocity = Vec3.ZERO;
        private float startScale = 1.0f;
        private float scaleModifier = 1.0f;
        private ParticleColorTransition color;
        private ParticleRollData roll;
        private int lifetime;

        public Builder startScale(float value) {
            startScale = value;
            return this;
        }

        public Builder scaleModifier(float value) {
            scaleModifier = value;
            return this;
        }

        public Builder scale(float startScale, float scaleModifier) {
            this.startScale = startScale;
            this.scaleModifier = scaleModifier;
            return this;
        }

        public Builder velocity(Vec3 value) {
            velocity = value;
            return this;
        }

        public Builder color(float r, float g, float b) {
            color = ParticleColorTransition.ofStaticColor(r, g, b);
            return this;
        }

        public Builder color(float r, float g, float b, float endR, float endG, float endB) {
            color = ParticleColorTransition.of(r, g, b, endR, endG, endB);
            return this;
        }

        public Builder roll(float startRoll) {
            roll = new ParticleRollData(startRoll, 0.0f);
            return this;
        }

        public Builder roll(float startRoll, float roll) {
            this.roll = new ParticleRollData(startRoll, roll);
            return this;
        }

        public Builder lifetime(int value) {
            lifetime = value;
            return this;
        }

        public ColoredSparkParticleEffect build() {
            return new ColoredSparkParticleEffect(velocity, startScale, scaleModifier, color, roll, lifetime);
        }

        public ColoredSparkParticleEffect buildBig() {
            return new BigColoredSparkParticleEffect(velocity, startScale, scaleModifier, color, roll, lifetime);
        }
    }

    static {
        CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Vec3.CODEC.fieldOf("velocity").forGetter(ColoredSparkParticleEffect::getVelocity),
                Codec.FLOAT.fieldOf("startScale").forGetter(ColoredSparkParticleEffect::getStartScale),
                Codec.FLOAT.fieldOf("scaleModifier").forGetter(ColoredSparkParticleEffect::getScaleModifier),
                ParticleColorTransition.CODEC.fieldOf("color").forGetter(ColoredSparkParticleEffect::getColor),
                ParticleRollData.CODEC.fieldOf("roll").forGetter(ColoredSparkParticleEffect::getRoll),
                Codec.INT.fieldOf("lifetime").forGetter(ColoredSparkParticleEffect::getLifetime)
        ).apply(instance, ColoredSparkParticleEffect::new));

        STREAM_CODEC = StreamCodec.composite(
                Vec3.STREAM_CODEC, ColoredSparkParticleEffect::getVelocity,
                ByteBufCodecs.FLOAT, ColoredSparkParticleEffect::getStartScale,
                ByteBufCodecs.FLOAT, ColoredSparkParticleEffect::getScaleModifier,
                ParticleColorTransition.STREAM_CODEC, ColoredSparkParticleEffect::getColor,
                ParticleRollData.STREAM_CODEC, ColoredSparkParticleEffect::getRoll,
                ByteBufCodecs.INT, ColoredSparkParticleEffect::getLifetime,
                ColoredSparkParticleEffect::new
        );
    }
}
