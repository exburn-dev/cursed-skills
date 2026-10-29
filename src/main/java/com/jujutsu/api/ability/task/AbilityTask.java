package com.jujutsu.api.ability.task;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface AbilityTask {
    InteractionResult execute(Player player);
}