package com.jujutsu.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;

public interface IVelocityParticleEffect extends ParticleOptions {
    Vec3 getVelocity();
    void setVelocity(Vec3 velocity);
}
