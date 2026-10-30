package com.jujutsu.sound.dynamic;

import net.minecraft.world.phys.Vec3;

public interface DynamicSoundSource {
    int getTick();
    Vec3 getPosition();
    float getNormalizedStress();
}
