package com.jujutsu.client.particle;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class LapseBlueCoreParticle extends SpriteBillboardParticle {
    protected final SpriteProvider spriteProvider;

    protected LapseBlueCoreParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider) {
        super(clientWorld, d, e + 0.1f, f);
        this.spriteProvider = spriteProvider;
        this.scale *= 5F;
        this.maxAge = 10;
        //this.setColor(255, 255, 255);
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        this.setSpriteForAge(this.spriteProvider);
        this.angle = 0;

        super.tick();
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(FabricSpriteProvider fabricSpriteProvider) {
            this.spriteProvider = fabricSpriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new LapseBlueCoreParticle(world, x, y, z, spriteProvider);
        }
    }
}