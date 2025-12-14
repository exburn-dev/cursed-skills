package com.jujutsu.client.hud;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.keybind.AdditionalInputSystem;
import com.jujutsu.systems.ability.data.RequestedInputKey;
import com.jujutsu.systems.ability.core.AbilityInstance;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.jujutsu.systems.ability.data.ClientData;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class AbilitiesHudRenderer {
    public static void render(DrawContext context, RenderTickCounter counter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client == null || client.player == null) return;

        IAbilitiesHolder holder = (IAbilitiesHolder) client.player;

        MatrixStack matrices = context.getMatrices();
        matrices.push();

        for(AbilitySlot slot: holder.getRunningSlots()) {
            AbilityInstance instance = holder.getAbilityInstance(slot);

            renderHud(instance, context, counter);
        }
        renderAdditionalInput(context, counter);
        matrices.pop();
    }

    private static void renderHud(AbilityInstance instance, DrawContext context, RenderTickCounter counter) {
        if(!instance.getStatus().isRunning() && !instance.getStatus().isWaiting()) return;

        ClientData clientData = instance.getType().getClientData();
        if(clientData != null && clientData.overlay() != null) {
            clientData.overlay().render(context, counter, instance);
        }
    }

    private static void renderAdditionalInput(DrawContext context, RenderTickCounter counter) {
        if(AdditionalInputSystem.getAdditionalInput().isEmpty()) return;

        for(AdditionalInputSystem.InputData inputData: AdditionalInputSystem.getAdditionalInput()) {
            RequestedInputKey input = inputData.input;
            if (input.mouseButton() >= 0) {
                int x = context.getScaledWindowWidth() / 2 - 8;
                int y = context.getScaledWindowHeight() / 2 - 8;
                Identifier texture = input.mouseButton() == 0 ? Jujutsu.id("additional_input/mouse_left")
                        : Jujutsu.id("additional_input/mouse_right");
                context.drawGuiTexture(texture, x, y, 16, 16);
            }
        }
    }
}
