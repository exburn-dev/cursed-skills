package com.jujutsu.systems.buff;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public interface BuffProvider {
    void apply(LivingEntity entity, Identifier buffId);
    void remove(LivingEntity entity, Identifier buffId);
}
