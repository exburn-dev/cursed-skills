package com.jujutsu.client.particle;

import net.minecraft.particle.ParticleEffect;
import org.joml.Vector3f;

public interface IVelocityParticleEffect extends ParticleEffect {
    Vector3f getVelocity();
    void setVelocity(Vector3f velocity);
}
