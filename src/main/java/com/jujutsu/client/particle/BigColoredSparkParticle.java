package com.jujutsu.client.particle;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

public class BigColoredSparkParticle extends ColoredSparkParticle {
    public BigColoredSparkParticle(ClientWorld clientWorld, double d, double e, double f, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider, ColoredSparkParticleEffect parameters) {
        super(clientWorld, d, e, f, velocityX, velocityY, velocityZ, spriteProvider, parameters);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }
}
