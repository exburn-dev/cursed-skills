package com.jujutsu.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LastAttackTimeAccessor {
    @Accessor("lastAttackTime")
    void setLastAttackTime(int lastAttackTime);
}
