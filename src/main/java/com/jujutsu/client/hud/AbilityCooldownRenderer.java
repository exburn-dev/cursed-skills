package com.jujutsu.client.hud;

import com.jujutsu.systems.ability.client.AbilityClientComponent;
import com.jujutsu.systems.ability.client.ClientComponentContainer;
import com.jujutsu.systems.ability.core.AbilityInstanceData;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class AbilityCooldownRenderer {
    private static final List<AbilitySlot> stack = new ArrayList<>();

    public static void render(DrawContext context, RenderTickCounter counter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        AbilityClientComponent component = ClientComponentContainer.abilityComponent;
        MatrixStack matrices = context.getMatrices();

        matrices.push();
        matrices.scale(0.75f, 0.75f, 1f);

        int maxPanelWidth = 160;
        int maxPanelHeight = 24;
        int panelWidth = Math.min(maxPanelWidth, stack.size() * 20);
        int panelHeight = maxPanelHeight;

        int panelX = 40;
        int panelY = context.getScaledWindowHeight() - panelHeight - 40;

        int iconWidth = 16;
        int iconHeight = 16;

        Identifier texture = Identifier.of("jujutsu", "textures/gui/square.png");
        for(int i = 0; i < stack.size(); i++) {
            AbilityInstanceData instance = component.get(stack.get(i));
            int x = panelX + i * iconWidth;
            int y = panelY;

            float progress = Math.min(((float) instance.cooldownTime()) / instance.maxCooldownTime(), 1f);

            RenderSystem.setShaderTexture(0, texture);

            ShaderUtils.abilityCooldownShader.getUniform("Progress").set(progress);
            ShaderUtils.abilityCooldownShader.addSampler("Sampler0", texture);

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();

            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_TEXTURE);

            Matrix4f mat = matrices.peek().getPositionMatrix();

            buf.vertex(mat, x, y, 0).texture(0, 0);
            buf.vertex(mat, x, y + iconHeight, 0).texture(0, 1);
            buf.vertex(mat, x + iconWidth, y, 0).texture(1, 0);
            buf.vertex(mat, x + iconWidth, y + iconHeight, 0).texture(1, 1);

            RenderSystem.setShader(() -> ShaderUtils.abilityCooldownShader);

            BufferRenderer.drawWithGlobalProgram(buf.end());

            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();

            //context.fill(x, y, x + iconWidth, y + iconHeight, 0xFFFFFFFF);
            context.drawCenteredTextWithShadow(client.textRenderer, Text.literal("abl"), x, y, 0xFFFFFF);
        }

        matrices.pop();
    }

    public static void onAbilitiesUpdated() {
        AbilityClientComponent component = ClientComponentContainer.abilityComponent;

        stack.removeIf(slot -> !component.get(slot).status().onCooldown());

        for(AbilitySlot slot: component.getSlots()) {
            AbilityInstanceData instance = component.get(slot);
            boolean onCooldown = instance.status().onCooldown();

            if(onCooldown && !stack.contains(slot)) {
                stack.add(slot);
            }
        }
    }

    private static class AbilityCooldownElement extends HudElement {
        private final TextRenderer textRenderer;
        private AbilityInstanceData instance;

        public AbilityCooldownElement(int x, int y, int width, int height, AbilityInstanceData instance, TextRenderer textRenderer) {
            super(x, y, width, height);
            this.instance = instance;
            this.textRenderer = textRenderer;
        }

        @Override
        public void renderElement(DrawContext context, RenderTickCounter counter, int x, int y) {
            int width = getBiggestWidth() + textRenderer.getWidth(" - 000");
            int cooldownInSeconds = instance.cooldownTime() / 20;

            int baseColor = 0x484a385c;
            int transparentColor = 0x004a385c;

            context.fill(x + 20, y, x + width, y + height, baseColor);

            fillHorizontalGradient(context, x, y, x + 20, y + height, transparentColor, baseColor);
            fillHorizontalGradient(context, x + width, y, x + width + 20, y + height, baseColor, transparentColor);

            context.drawCenteredTextWithShadow(textRenderer, Text.empty().append(instance.type().getName())
                    .append(Text.literal(String.format(" - %s", cooldownInSeconds))), x + 10 + width / 2, y - 2 + height / 2, 0x00FFFFFF);
        }

        private void fillHorizontalGradient(DrawContext context, float startX, float startY, float endX, float endY, int colorStart, int colorEnd) {
            VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(RenderLayer.getGui());
            Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
            float z = 0;

            vertexConsumer.vertex(matrix4f, startX, startY, z).color(colorStart);
            vertexConsumer.vertex(matrix4f, startX, endY, z).color(colorStart);
            vertexConsumer.vertex(matrix4f, endX, endY, z).color(colorEnd);
            vertexConsumer.vertex(matrix4f, endX, startY, z).color(colorEnd);

            context.getVertexConsumers().draw();
        }

        public void setInstance(AbilityInstanceData instance) {
            this.instance = instance;
        }

        private int getBiggestWidth() {
            int width = 10;
            for(int i = 0; i < stack.size(); i++) {
                int length = textRenderer.getWidth(instance.type().getName());
                if(length > width) {
                    width += length;
                }
            }
            return width;
        }
    }

    private static abstract class HudElement {
        public final long startTime;
        public int width;
        public int height;
        private int x;
        private int y;

        public HudElement(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startTime = Util.getMeasuringTimeMs();
        }

        public void render(DrawContext context, RenderTickCounter counter, int x, int y) {
            renderElement(context, counter, x + this.x, y + this.y);
        }

        public abstract void renderElement(DrawContext context, RenderTickCounter counter, int x, int y);
    }
}
