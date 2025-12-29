package com.jujutsu.event;

import com.jujutsu.event.client.CameraEvents;
import com.jujutsu.event.client.HandRenderingEvents;
import com.jujutsu.event.client.TooltipRenderingEvents;
import com.jujutsu.registry.ModAbilities;
import com.jujutsu.systems.ability.client.AbilityClientComponent;
import com.jujutsu.systems.ability.client.ClientComponentContainer;
import com.jujutsu.systems.ability.core.AbilityInstanceData;
import com.jujutsu.systems.ability.data.ClientData;
import com.jujutsu.item.IBorderTooltipItem;
import com.jujutsu.screen.HandTransformSettingScreen;
import com.jujutsu.util.HandAnimationUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;
import net.minecraft.util.math.RotationAxis;

public class ClientEventListeners {
    public static void register() {
        HandRenderingEvents.HAND_RENDER_EVENT.register((matrices, vertexConsumers, player, playerEntityRenderer, equipProgress, swingProgress, light) -> {
            AbilityClientComponent component = ClientComponentContainer.abilityComponent;

            for(AbilityInstanceData instance : component.all()) {
                if(!instance.status().isRunning() && !instance.status().isWaiting()) continue;

                ClientData clientData = instance.type().getClientData();
                if(clientData != null && clientData.animation() != null) {
                    boolean rendered = clientData.animation().render(matrices, vertexConsumers, instance, player, playerEntityRenderer, equipProgress, swingProgress, light);
                    if(rendered) {
                        return ActionResult.SUCCESS;
                    }
                }
            }
            return ActionResult.PASS;
        });

        TooltipRenderingEvents.RENDER_TOOLTIP_EVENT.register((context, stack, x, y, width, height) -> {
            if(!(stack.getItem() instanceof IBorderTooltipItem borderTooltipItem)) return ActionResult.PASS;
            int z = 500;

            IBorderTooltipItem.TooltipBorderData data = borderTooltipItem.getBorderData();
            int offset = borderTooltipItem.getOffset();
            x -= offset; y -= offset;
            width += offset * 2; height += offset * 2;

            if(data.mainBorder() != null) context.drawGuiTexture(data.mainBorder(), x, y, z, width , height);
            if(data.upperTile() != null) context.drawGuiTexture(data.upperTile(), x + 4, y, z + 1, width - 8 , 4);
            if(data.upperDecorTile() != null) context.drawGuiTexture(data.upperDecorTile(), x + 4 + width / 4, y, z + 2, width / 4 * 2 - 8 , 4);

            if(data.bottomTile() != null) context.drawGuiTexture(data.bottomTile(), x + 4, y + height - 4, z + 1, width - 8 , 4);
            if(data.bottomDecorTile() != null) context.drawGuiTexture(data.bottomDecorTile(), x + 4 + width / 4, y + height - 4, z + 2, width / 4 * 2 - 8 , 4);

            if(data.upperCentralElement() != null) {
                int elementWidth = (int) (data.upperCentralElement().width() * data.upperCentralElement().scale());
                int elementHeight = (int) (data.upperCentralElement().height() * data.upperCentralElement().scale());

                context.drawGuiTexture(data.upperCentralElement().texture(), x + width / 2 - elementWidth / 2, y - elementHeight / 2, z + 10, elementWidth, elementHeight);
            }
            if(data.bottomCentralElement() != null)  {
                int elementWidth = (int) (data.bottomCentralElement().width() * data.bottomCentralElement().scale());
                int elementHeight = (int) (data.bottomCentralElement().height() * data.bottomCentralElement().scale());

                context.drawGuiTexture(data.bottomCentralElement().texture(), x + width / 2 - elementWidth / 2, y - elementHeight / 2, z + 10, elementWidth, elementHeight);
            }

            borderTooltipItem.render(context, x, y, width, height);

            return ActionResult.PASS;
        });

        HandRenderingEvents.HAND_RENDER_EVENT.register((matrices, vertexConsumers, player, playerEntityRenderer, equipProgress, swingProgress, light) -> {
            if(!(MinecraftClient.getInstance().currentScreen instanceof HandTransformSettingScreen)) return ActionResult.PASS;

            HandAnimationUtils.applyDefaultHandTransform(matrices, HandTransformSettingScreen.BEFORE);

            matrices.translate(HandTransformSettingScreen.TRANSLATE_X, HandTransformSettingScreen.TRANSLATE_Y, HandTransformSettingScreen.TRANSLATE_Z);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) HandTransformSettingScreen.ROTATE_X));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) HandTransformSettingScreen.ROTATE_Y));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) HandTransformSettingScreen.ROTATE_Z));

            if (HandTransformSettingScreen.BEFORE) {
                playerEntityRenderer.renderRightArm(matrices, vertexConsumers, light, player);
            } else {
                playerEntityRenderer.renderLeftArm(matrices, vertexConsumers, light, player);
            }

            return ActionResult.SUCCESS;
        });

        CameraEvents.GET_CAMERA_BONUS_EVENT.register(player -> {
            float cameraRestriction = 0;
            float cameraSpeed = 0;

            AbilityClientComponent component = ClientComponentContainer.abilityComponent;
            if(component.isRunning(ModAbilities.HOLLOW_PURPLE) || component.isRunning(ModAbilities.REVERSAL_RED) || component.isRunning(ModAbilities.LAPSE_BLUE)) {
                cameraRestriction -= 350;
                cameraSpeed -= 0.75f;
            }

            return new Pair<>(ActionResult.PASS, new CameraEvents.CameraBonus(cameraRestriction, cameraSpeed));
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(client == null || client.player == null) return;

            for(AbilityInstanceData data : ClientComponentContainer.abilityComponent.all()) {
                if(data.status().onCooldown()) {
                    data.setCooldownTime(data.cooldownTime() + 1);
                }
            }
        });
    }
}
