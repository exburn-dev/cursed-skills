package com.jujutsu.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;

public class CameraEvents {
    public static final Event<GetCameraBonusCallback> GET_CAMERA_BONUS_EVENT = EventFactory.createArrayBacked(GetCameraBonusCallback.class,
            (listeners) -> (player) -> {
                float cameraRestriction = 0;
                float cameraSpeed = 0;
                for(GetCameraBonusCallback listener: listeners) {
                    Pair<ActionResult, CameraBonus> result = listener.interact(player);
                    cameraRestriction += result.getRight().cameraRestriction();
                    cameraSpeed += result.getRight().cameraSpeed();

                    if(result.getLeft() != ActionResult.PASS) {
                        return result;
                    }
                }

                return new Pair<>(ActionResult.PASS, new CameraBonus(cameraRestriction, cameraSpeed));
            });

    @FunctionalInterface
    public interface GetCameraBonusCallback {
        Pair<ActionResult, CameraBonus> interact(ClientPlayerEntity player);
    }

    public record CameraBonus(float cameraRestriction, float cameraSpeed) {}
}
