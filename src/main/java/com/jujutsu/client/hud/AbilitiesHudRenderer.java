package com.jujutsu.client.hud;

import com.jujutsu.api.ability.client.render.AbilityClientRenderers;
import com.jujutsu.api.ability.core.AbilityInstance;
import com.jujutsu.client.input.InputRequestSystem;
import com.jujutsu.api.ability.client.AbilityClientComponent;
import com.jujutsu.api.ability.client.ClientComponentContainer;
import com.jujutsu.api.ability.data.RequestedInputKey;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;

public class AbilitiesHudRenderer {
    public static void extract(GuiGraphics graphics, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        AbilityClientComponent component = ClientComponentContainer.abilityComponent;

        Matrix3x2fStack matrices = graphics.pose();
        matrices.pushMatrix();

        for (AbilityInstance instance : component.all()) {
            renderAbilityHud(graphics, tickCounter, instance);
        }
        renderInputRequestHud(graphics, tickCounter);

        matrices.popMatrix();
    }

    private static void renderAbilityHud(GuiGraphics graphics, DeltaTracker tickCounter, AbilityInstance instance) {
        if (!instance.isExecuted()) return;

        AbilityClientRenderers renderers = AbilityClientComponent.getRenderers(instance.type());
        if (renderers != null && renderers.hasOverlay()) {
            renderers.overlay().render(graphics, tickCounter, instance);
        }
    }

    private static void renderInputRequestHud(GuiGraphics graphics, DeltaTracker tickCounter) {
        if(InputRequestSystem.isEmpty()) return;

        for(RequestedInputKey input : InputRequestSystem.getAdditionalInput()) {
            if(input.mouseButton() >= 0) {
                int x = graphics.guiWidth() / 2 - 8;
                int y = graphics.guiHeight() / 2 - 8;
                Identifier texture = input.mouseButton() == 0
                        ? InputRequestSystem.LMB_ICON_TEXTURE
                        : InputRequestSystem.RMB_ICON_TEXTURE;

                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, 16, 16);
            }
        }
    }
}
