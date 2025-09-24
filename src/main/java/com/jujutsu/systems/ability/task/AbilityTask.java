package com.jujutsu.systems.ability.task;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

@FunctionalInterface
public interface AbilityTask {
    ActionResult execute(PlayerEntity player);
}