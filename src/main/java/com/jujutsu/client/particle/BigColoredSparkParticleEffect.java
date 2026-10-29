package com.jujutsu.client.particle;

import com.jujutsu.client.particle.data.ParticleColorTransition;
import com.jujutsu.client.particle.data.ParticleRollData;
import com.jujutsu.registry.ModParticleTypes;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class BigColoredSparkParticleEffect extends ColoredSparkParticleEffect {
    public static final MapCodec<BigColoredSparkParticleEffect> CODEC;
    public static final StreamCodec<FriendlyByteBuf, BigColoredSparkParticleEffect> STREAM_CODEC;

    public BigColoredSparkParticleEffect(float startScale, float scaleModifier, ParticleColorTransition color, float startRoll, float roll, int lifetime) {
        super(startScale, scaleModifier, color, startRoll, roll, lifetime);
    }

    public BigColoredSparkParticleEffect(Vec3 velocity, float startScale, float scaleModifier, ParticleColorTransition color, ParticleRollData roll, int lifetime) {
        super(velocity, startScale, scaleModifier, color, roll, lifetime);
    }

    public BigColoredSparkParticleEffect(ColoredSparkParticleEffect coloredSpark) {
        super(
                coloredSpark.getStartScale(),
                coloredSpark.getScaleModifier(),
                coloredSpark.getColor(),
                coloredSpark.getRoll().startRoll(),
                coloredSpark.getRoll().roll(),
                coloredSpark.getLifetime()
        );
    }

    private static ColoredSparkParticleEffect toColoredSpark(BigColoredSparkParticleEffect self) {
        return new ColoredSparkParticleEffect(
                self.getStartScale(),
                self.getScaleModifier(),
                self.getColor(),
                self.getRoll().startRoll(),
                self.getRoll().roll(),
                self.getLifetime()
        );
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticleTypes.BIG_COLORED_SPARK;
    }

    public static class Factory implements ParticleFactory<BigColoredSparkParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(FabricSpriteProvider fabricSpriteProvider) {
            this.spriteProvider = fabricSpriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(BigColoredSparkParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            Vector3f velocity = parameters.getVelocity();
            return new BigColoredSparkParticle(world, x, y, z, velocity.x, velocity.y, velocity.z, spriteProvider, parameters);
        }
    }

    static {
        CODEC = ColoredSparkParticleEffect.CODEC.xmap(BigColoredSparkParticleEffect::new, BigColoredSparkParticleEffect::toColoredSpark);
        STREAM_CODEC = ColoredSparkParticleEffect.STREAM_CODEC.map(BigColoredSparkParticleEffect::new, BigColoredSparkParticleEffect::toColoredSpark);
    }
}
