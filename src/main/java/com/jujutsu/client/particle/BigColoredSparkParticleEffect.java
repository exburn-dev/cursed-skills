package com.jujutsu.client.particle;

import com.jujutsu.registry.ModParticleTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class BigColoredSparkParticleEffect extends ColoredSparkParticleEffect {
    public static final MapCodec<BigColoredSparkParticleEffect> CODEC;
    public static final PacketCodec<RegistryByteBuf, BigColoredSparkParticleEffect> PACKET_CODEC;

    public BigColoredSparkParticleEffect(float startScale, float scaleModifier, ColorTransition color, float startRoll, float roll, int lifetime) {
        super(startScale, scaleModifier, color, startRoll, roll, lifetime);
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
            return new ColoredSparkParticle(world, x, y, z, velocity.x, velocity.y, velocity.z, spriteProvider, parameters);
        }
    }

    static {
        CODEC = ColoredSparkParticleEffect.CODEC.xmap(BigColoredSparkParticleEffect::new, BigColoredSparkParticleEffect::toColoredSpark);
        PACKET_CODEC = ColoredSparkParticleEffect.PACKET_CODEC.xmap(BigColoredSparkParticleEffect::new, BigColoredSparkParticleEffect::toColoredSpark);
    }
}
