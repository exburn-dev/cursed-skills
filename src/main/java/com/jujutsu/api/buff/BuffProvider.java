package com.jujutsu.api.buff;


import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

public interface BuffProvider {
    void apply(LivingEntity entity, Identifier buffId);
    void remove(LivingEntity entity, Identifier buffId);
}
