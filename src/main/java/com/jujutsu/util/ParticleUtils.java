package com.jujutsu.util;

import com.jujutsu.client.particle.IVelocityParticleEffect;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class ParticleUtils {
    public static void createBall(Supplier<ParticleOptions> particleSupplier, Vec3 vec, Level level, int particleCount, float radius, float velocity) {
        for (int i = 0; i < particleCount; i++) {
            ParticleOptions particle = particleSupplier.get();
            double theta = 2 * Math.PI * Math.random();
            double phi = Math.acos(2 * Math.random() - 1);

            double x = vec.x + radius * Math.sin(phi) * Math.cos(theta);
            double y = vec.y + radius * Math.sin(phi) * Math.sin(theta);
            double z = vec.z + radius * Math.cos(phi);

            Vec3 velocityVec = new Vec3(x, y, z).subtract(vec).normalize().scale(velocity);

            spawnParticle(particle, new Vec3(x, y, z), level, velocityVec);
        }
    }

    public static void createCyl(Supplier<ParticleOptions> particleSupplier, Vec3 pos, Level level, int particleCount, float radius, float velocity) {
        for (int i = 0; i < particleCount; i++) {
            ParticleOptions particle = particleSupplier.get();
            double angle = 2 * Math.PI * i / particleCount;

            double x = pos.x + radius * Math.cos(angle);
            double y = pos.y;
            double z = pos.z + radius * Math.sin(angle);

            Vec3 velocityVec = new Vec3(x, y, z).subtract(pos).normalize().scale(velocity);

            spawnParticle(particle, new Vec3(x, y, z), level, velocityVec);
        }
    }

    public static void createBallSurface(Supplier<ParticleOptions> particleSupplier, Vec3 pos, Level level, int particleCount, float radius, float velocity) {
        for (int i = 0; i < particleCount; i++) {
            ParticleOptions particle = particleSupplier.get();
            double theta = 2 * Math.PI * Math.random();
            double phi = Math.acos(2 * Math.random() - 1);

            double x = pos.x + radius * Math.sin(phi) * Math.cos(theta);
            double y = pos.y + radius * Math.cos(phi);
            double z = pos.z + radius * Math.sin(phi) * Math.sin(theta);

            Vec3 velocityVec = new Vec3(x, y, z).subtract(pos).normalize().scale(velocity);

            level.addParticle(particle, x, y, z, velocityVec.x, velocityVec.y, velocityVec.z);
        }
    }

    public static void spawnParticle(ParticleOptions particle, Vec3 pos, Level level, Vec3 velocity) {
        if(particle instanceof IVelocityParticleEffect velocityParticle) {
            velocityParticle.setVelocity(new Vec3(velocity.x, velocity.y, velocity.z));
        }

        if(level.isClientSide()) {
            level.addParticle(particle, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
        }
        else {
            ((ServerLevel) level).sendParticles(particle, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
        }
    }

    public static void renderVec(Vec3 vec, Vec3 pos, Level level) {
        double length = vec.length();
        Vec3 normalized = vec.normalize();

        for(double i = 0; i < length; i += 0.5) {
            Vec3 vec1 = normalized.scale(i);
            Vec3 particlePos = pos.add(vec1);

            spawnParticle(new DustParticleOptions(0xFF0000, 1), particlePos, level, Vec3.ZERO);
        }
    }
}
