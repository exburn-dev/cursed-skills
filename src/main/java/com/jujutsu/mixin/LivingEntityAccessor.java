package com.jujutsu.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("lastAttackTime")
    void setLastAttackTime(int lastAttackTime);

    @Invoker("setLivingFlag")
    void invokeSetLivingFlag(int mask, boolean value);
}
