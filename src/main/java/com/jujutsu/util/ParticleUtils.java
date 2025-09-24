package com.jujutsu.util;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.particle.IVelocityParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class ParticleUtils {
    public static void createBall(Supplier<ParticleEffect> particleSupplier, Vec3d vec, World world, int particleCount, float radius, float velocity) {
        for (int i = 0; i < particleCount; i++) {
            ParticleEffect particle = particleSupplier.get();
            double theta = 2 * Math.PI * Math.random();
            double phi = Math.acos(2 * Math.random() - 1);

            double x = vec.x + radius * Math.sin(phi) * Math.cos(theta);
            double y = vec.y + radius * Math.sin(phi) * Math.sin(theta);
            double z = vec.z + radius * Math.cos(phi);

            Vec3d velocityVec = new Vec3d(x, y, z).subtract(vec).normalize().multiply(velocity);

            spawnParticle(particle, new Vec3d(x, y, z), world, velocityVec);
        }
    }

    public static void createCyl(Supplier<ParticleEffect> particleSupplier, Vec3d pos, World world, int particleCount, float radius, float velocity) {
        for (int i = 0; i < particleCount; i++) {
            ParticleEffect particle = particleSupplier.get();
            double angle = 2 * Math.PI * i / particleCount;

            double x = pos.x + radius * Math.cos(angle);
            double y = pos.y;
            double z = pos.z + radius * Math.sin(angle);

            Vec3d velocityVec = new Vec3d(x, y, z).subtract(pos).normalize().multiply(velocity);

            spawnParticle(particle, new Vec3d(x, y, z), world, velocityVec);
        }
    }

    public static void createBallSurface(Supplier<ParticleEffect> particleSupplier, Vec3d pos, World world, int particleCount, float radius, float velocity) {
        for (int i = 0; i < particleCount; i++) {
            ParticleEffect particle = particleSupplier.get();
            double theta = 2 * Math.PI * Math.random();
            double phi = Math.acos(2 * Math.random() - 1);

            double x = pos.x + radius * Math.sin(phi) * Math.cos(theta);
            double y = pos.y + radius * Math.cos(phi);
            double z = pos.z + radius * Math.sin(phi) * Math.sin(theta);

            Vec3d velocityVec = new Vec3d(x, y, z).subtract(pos).normalize().multiply(velocity);

            world.addParticle(particle, x, y, z, velocityVec.x, velocityVec.y, velocityVec.z);
        }
    }

    public static void spawnParticle(ParticleEffect particle, Vec3d pos, World world, Vec3d velocity) {
        if(particle instanceof IVelocityParticleEffect velocityParticle) {
            velocityParticle.setVelocity(new Vector3f((float) velocity.x, (float) velocity.y, (float) velocity.z));
        }

        if(world.isClient()) {
            world.addParticle(particle, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
        }
        else {
            ((ServerWorld) world).spawnParticles(particle, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
        }
    }

    public static void renderVec(Vec3d vec, Vec3d pos, World world) {
        double length = vec.length();
        Vec3d normalized = vec.normalize();

        for(double i = 0; i < length; i += 0.5) {
            Vec3d vec1 = normalized.multiply(i);
            Vec3d particlePos = pos.add(vec1);

            spawnParticle(new DustParticleEffect(new Vector3f(1, 0, 0), 1), particlePos, world, Vec3d.ZERO);
        }
    }
}
