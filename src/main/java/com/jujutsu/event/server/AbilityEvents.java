package com.jujutsu.event.server;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;

public class AbilityEvents {
    public static final Event<OnPreventDying> ON_PREVENT_DYING = EventFactory.createArrayBacked(OnPreventDying.class,
            (listeners) -> (player, attacker, amount) -> {
                for(OnPreventDying listener: listeners) {
                    ActionResult result = listener.interact(player, attacker, amount);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    @FunctionalInterface
    public interface OnPreventDying {
        ActionResult interact(PlayerEntity player, Entity attacker, float amount);
    }
}
