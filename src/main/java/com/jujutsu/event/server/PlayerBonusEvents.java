package com.jujutsu.event.server;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;

public class PlayerBonusEvents {
    public static final Event<GetDamageBonusCallback> GET_DAMAGE_BONUS_EVENT = EventFactory.createArrayBacked(GetDamageBonusCallback.class,
            (listeners) -> (player, entity) -> {
                float bonus = 0;
                for(GetDamageBonusCallback listener: listeners) {
                    Pair<ActionResult, Float> result = listener.interact(player, entity);
                    bonus += result.getRight();

                    if(result.getLeft() != ActionResult.PASS) {
                        return result;
                    }
            }

            return new Pair<>(ActionResult.PASS, bonus);
    });

    public static final Event<GetSpeedBonusCallback> GET_SPEED_BONUS_EVENT = EventFactory.createArrayBacked(GetSpeedBonusCallback.class,
            (listeners) -> (player) -> {
                float bonus = 0;
                for(GetSpeedBonusCallback listener: listeners) {
                    Pair<ActionResult, Float> result = listener.interact(player);
                    bonus += result.getRight();

                    if(result.getLeft() != ActionResult.PASS) {
                        return result;
                    }
            }

            return new Pair<>(ActionResult.PASS, bonus);
    });

    @FunctionalInterface
    public interface GetDamageBonusCallback {
        Pair<ActionResult, Float> interact(PlayerEntity player, LivingEntity entity);
    }

    @FunctionalInterface
    public interface GetSpeedBonusCallback {
        Pair<ActionResult, Float> interact(PlayerEntity player);
    }
}
