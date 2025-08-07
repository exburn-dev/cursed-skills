package com.jujutsu.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.ActionResult;

public final class HandRenderingEvents {
    public static final Event<HandRenderCallback> HAND_RENDER_EVENT = EventFactory.createArrayBacked(HandRenderCallback.class,
            (listeners) -> (matrices, vertexConsumers, player, playerEntityRenderer, equipProgress, swingProgress, light) -> {
                for(HandRenderCallback listener: listeners) {
                    ActionResult result = listener.interact(matrices, vertexConsumers, player, playerEntityRenderer, equipProgress, swingProgress, light);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    @FunctionalInterface
    public interface HandRenderCallback {
        ActionResult interact(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ClientPlayerEntity player, PlayerEntityRenderer playerEntityRenderer, float equipProgress, float swingProgress, int light);
    }
}
