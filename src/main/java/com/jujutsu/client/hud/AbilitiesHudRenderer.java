package com.jujutsu.client.hud;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.keybind.InputRequestSystem;
import com.jujutsu.systems.ability.client.AbilityClientComponent;
import com.jujutsu.systems.ability.client.ClientComponentContainer;
import com.jujutsu.systems.ability.core.AbilityInstanceData;
import com.jujutsu.systems.ability.data.RequestedInputKey;
import com.jujutsu.systems.ability.data.ClientData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class AbilitiesHudRenderer {
    public static void render(DrawContext context, RenderTickCounter counter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client == null || client.player == null) return;

        AbilityClientComponent component = ClientComponentContainer.abilityComponent;

        MatrixStack matrices = context.getMatrices();
        matrices.push();

        for(AbilityInstanceData instanceData: component.all()) {
            renderHud(instanceData, context, counter);
        }
        renderAdditionalInput(context, counter);
        matrices.pop();
    }

    private static void renderHud(AbilityInstanceData instance, DrawContext context, RenderTickCounter counter) {
        if(!instance.status().isRunning() && !instance.status().isWaiting()) return;

        ClientData clientData = instance.type().getClientData();
        if(clientData != null && clientData.overlay() != null) {
            clientData.overlay().render(context, counter, instance);
        }
    }

    private static void renderAdditionalInput(DrawContext context, RenderTickCounter counter) {
        if(InputRequestSystem.getAdditionalInput().isEmpty()) return;

        for(RequestedInputKey input: InputRequestSystem.getAdditionalInput()) {
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
