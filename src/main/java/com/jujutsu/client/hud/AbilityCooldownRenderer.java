package com.jujutsu.client.hud;

import com.jujutsu.systems.ability.core.AbilityInstance;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AbilityCooldownRenderer {
    private static final HashMap<AbilitySlot, AbilityCooldownElement> cooldownElements = new HashMap<>();
    private static final List<AbilityCooldownElement> stack = new ArrayList<>();

    public static void render(DrawContext context, RenderTickCounter counter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        IAbilitiesHolder holder = (IAbilitiesHolder) client.player;
        for(AbilitySlot slot: holder.getSlots()) {
            boolean onCooldown = holder.onCooldown(slot);
            AbilityInstance instance = holder.getAbilityInstance(slot);

            if(cooldownElements.containsKey(slot) && cooldownElements.get(slot).instance != instance) {
                cooldownElements.get(slot).setInstance(instance);
            }

            if(onCooldown && !cooldownElements.containsKey(slot)) {
                var element = new AbilityCooldownElement(20, 300, 30, 20, instance, client.textRenderer);

                cooldownElements.put(slot, element);
                stack.add(element);
            }
            else if(!onCooldown && cooldownElements.containsKey(slot)) {
                stack.remove(cooldownElements.get(slot));
                cooldownElements.remove(slot);
            }
        }

        MatrixStack matrices = context.getMatrices();

        matrices.push();
        matrices.scale(0.75f, 0.75f, 1f);
        for(int i = 0; i < stack.size(); i++) {
            matrices.push();

            AbilityCooldownElement element = stack.get(i);
            long time = Util.getMeasuringTimeMs() - element.startTime;
            matrices.translate(0, MathHelper.clampedLerp(-50 - (stack.size() - 1) * 30 , 120 - i * 20, time / 500f), 0);
            element.render(context, counter, 0, -5 * i);

            matrices.pop();
        }
        matrices.pop();
    }

    private static class AbilityCooldownElement extends HudElement {
        private final TextRenderer textRenderer;
        private AbilityInstance instance;

        public AbilityCooldownElement(int x, int y, int width, int height, AbilityInstance instance, TextRenderer textRenderer) {
            super(x, y, width, height);
            this.instance = instance;
            this.textRenderer = textRenderer;
        }

        @Override
        public void renderElement(DrawContext context, RenderTickCounter counter, int x, int y) {
            int width = getBiggestWidth() + textRenderer.getWidth(" - 000");
            int cooldownInSeconds = instance.getCooldownTime() / 20;

            context.fill(x + 20, y, x + width, y + height, 0x485A5AEE);

            fillHorizontalGradient(context, x, y, x + 20, y + height, 0x005A5AEE, 0x485A5AEE);
            fillHorizontalGradient(context, x + width, y, x + width + 20, y + height, 0x485A5AEE, 0x005A5AEE);

            context.drawCenteredTextWithShadow(textRenderer, Text.empty().append(instance.getType().getName())
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

        public void setInstance(AbilityInstance instance) {
            this.instance = instance;
        }

        private int getBiggestWidth() {
            int width = 10;
            for(int i = 0; i < stack.size(); i++) {
                int length = textRenderer.getWidth(instance.getType().getName());
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
