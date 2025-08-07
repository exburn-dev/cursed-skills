package com.jujutsu.client.particle;

import com.jujutsu.registry.ModParticleTypes;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;

import java.util.List;

@Environment(EnvType.CLIENT)
public  class HollowPurpleParticle extends NoRenderParticle {
    private int age;
    private final double size;
    private final ParticleManager particleManager;
    private final List<FireworkExplosionComponent> explosions;;

    public HollowPurpleParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, ParticleManager particleManager, List<FireworkExplosionComponent> fireworkExplosions) {
        this(world, x, y, z, 0.5, velocityX, velocityY, velocityZ, particleManager, fireworkExplosions);
    }

    public HollowPurpleParticle(ClientWorld world, double x, double y, double z, double size, double velocityX, double velocityY, double velocityZ, ParticleManager particleManager, List<FireworkExplosionComponent> fireworkExplosions) {
        super(world, x, y, z);
        this.size = size;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.particleManager = particleManager;
        if (fireworkExplosions.isEmpty()) {
            throw new IllegalArgumentException("Cannot create firework starter with no explosions");
        } else {
            this.explosions = fireworkExplosions;
            this.maxAge = 1;
        }
    }

    public void tick() {
        if (this.age % 2 == 0 && this.age / 2 < this.explosions.size()) {
            int i = this.age / 2;
            FireworkExplosionComponent fireworkExplosionComponent2 = this.explosions.get(i);
            boolean bl3 = fireworkExplosionComponent2.hasTrail();
            boolean bl4 = fireworkExplosionComponent2.hasTwinkle();
            IntList intList = fireworkExplosionComponent2.colors();
            IntList intList2 = fireworkExplosionComponent2.fadeColors();
            if (intList.isEmpty()) {
                intList = IntList.of(DyeColor.BLACK.getFireworkColor());
            }

            this.explodeBall(size, 4, intList, intList2, this.velocityX, this.velocityY, this.velocityZ, bl3, bl4);

            int j = intList.getInt(0);
            Particle particle = this.particleManager.addParticle(ParticleTypes.FLASH, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            particle.setColor((float) ColorHelper.Argb.getRed(j) / 255.0F, (float)ColorHelper.Argb.getGreen(j) / 255.0F, (float)ColorHelper.Argb.getBlue(j) / 255.0F);
        }

        ++this.age;
        if (this.age > this.maxAge) {
            this.markDead();
        }

    }

    private void addExplosionParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, IntList colors, IntList targetColors, boolean trail, boolean flicker) {
        Explosion explosion = (Explosion)this.particleManager.addParticle(ModParticleTypes.HOLLOW_PURPLE_PARTICLE, x, y, z, velocityX, velocityY, velocityZ);
        explosion.setTrail(trail);
        explosion.setFlicker(flicker);
        explosion.setExplosionAlpha(0.99F);
        explosion.setColor((Integer) Util.getRandom(colors, this.random));
        if (!targetColors.isEmpty()) {
            explosion.setTargetColor((Integer)Util.getRandom(targetColors, this.random));
        }

    }

    private void explodeBall(double size, int amount, IntList colors, IntList targetColors, double velocityX, double velocityY, double velocityZ, boolean trail, boolean flicker) {
        double d = this.x;
        double e = this.y;
        double f = this.z;

        for(int i = -amount; i <= amount; ++i) {
            for(int j = -amount; j <= amount; ++j) {
                for(int k = -amount; k <= amount; ++k) {
                    double g = (double)j + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                    double h = (double)i + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                    double l = (double)k + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                    double m = Math.sqrt(g * g + h * h + l * l) / size + this.random.nextGaussian() * 0.05;
                    this.addExplosionParticle(d, e, f, g / m + velocityX, h / m + velocityY, l / m + velocityZ, colors, targetColors, trail, flicker);
                    //this.addExplosionParticle(d, e, f, velocityX, velocityY, velocityZ, colors, targetColors, trail, flicker);
                    if (i != -amount && i != amount && j != -amount && j != amount) {
                        k += amount * 2 - 1;
                    }
                }
            }
        }

    }

    @Environment(EnvType.CLIENT)
    public static class ExplosionFactory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public ExplosionFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Explosion explosion = new Explosion(clientWorld, d, e, f, g, h, i, MinecraftClient.getInstance().particleManager, this.spriteProvider);
            explosion.setExplosionAlpha(0.99F);
            return explosion;
        }
    }


    @Environment(EnvType.CLIENT)
    private static class Explosion extends AnimatedParticle {
        private boolean trail;
        private boolean flicker;
        private final ParticleManager particleManager;

        Explosion(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, ParticleManager particleManager, SpriteProvider spriteProvider) {
            super(world, x, y, z, spriteProvider, 0.1F);
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.velocityZ = velocityZ;
            this.particleManager = particleManager;
            this.scale *= 0.75F;
            //this.maxAge = 48 + this.random.nextInt(12);
            this.maxAge = 13;
            this.setSpriteForAge(spriteProvider);
        }

        public void setTrail(boolean trail) {
            this.trail = trail;
        }

        public void setFlicker(boolean flicker) {
            this.flicker = flicker;
        }

        public void setExplosionAlpha(float alpha) {
            this.setAlpha(alpha);
        }

        public void tick() {
            super.tick();
            if (this.trail && this.age < this.maxAge / 2 && (this.age + this.maxAge) % 2 == 0) {
                Explosion explosion = new Explosion(this.world, this.x, this.y, this.z, velocityX, velocityY, velocityZ, this.particleManager, this.spriteProvider);
                explosion.setAlpha(0.99F);
                explosion.setColor(this.red, this.green, this.blue);
                explosion.age = explosion.maxAge / 2;
//                if (this.field_3802) {
//                    explosion.field_3802 = true;
//                    explosion.field_3801 = this.field_3801;
//                    explosion.field_3800 = this.field_3800;
//                    explosion.field_3799 = this.field_3799;
//                }

                explosion.flicker = this.flicker;
                this.particleManager.addParticle(explosion);
            }

        }
    }

}