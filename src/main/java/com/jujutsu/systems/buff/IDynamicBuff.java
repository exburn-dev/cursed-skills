package com.jujutsu.systems.buff;

import net.minecraft.entity.LivingEntity;

public interface IDynamicBuff extends IBuff {
    double getValue(LivingEntity entity);
}
