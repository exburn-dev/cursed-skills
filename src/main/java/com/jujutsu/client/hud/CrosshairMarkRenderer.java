package com.jujutsu.client.hud;

import com.jujutsu.Jujutsu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

public class CrosshairMarkRenderer {
    private static CrosshairMarkData markData = null;

    private static int currentTime = 0;
    private static float prevTickDelta = 0;

    public static void addMarkData(CrosshairMarkData markData) {
        CrosshairMarkRenderer.markData = markData;
    }

    public static void render(DrawContext context, RenderTickCounter counter) {
        if(markData == null) return;

        if(currentTime >= markData.getTotalTime()) {
            markData = null;
            currentTime = 0;
            return;
        }

        float alpha = getAlpha(markData);
        float r = ColorHelper.Argb.getRed(markData.color()) / 255f;
        float g = ColorHelper.Argb.getGreen(markData.color()) / 255f;
        float b = ColorHelper.Argb.getBlue(markData.color()) / 255f;

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(r, g, b, alpha);
        RenderSystem.enableBlend();

        context.drawGuiTexture(Jujutsu.getId("hud/crosshair_mark"), context.getScaledWindowWidth() / 2 - 8, context.getScaledWindowHeight() / 2 - 8, 16, 16);

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1,1);

        if(prevTickDelta > counter.getTickDelta(true)) {
            currentTime++;
        }
        prevTickDelta = counter.getTickDelta(true);
    }

    private static float getAlpha(CrosshairMarkData markData) {
        int totalTime = markData.getTotalTime();
        float maxAlpha = 1;
        float alpha;

        if(currentTime >= totalTime - markData.fadeOut()) {
            alpha = MathHelper.clampedLerp(maxAlpha, 0, (float) (currentTime - totalTime + markData.fadeOut()) / 20);
        }
        else {
            alpha = MathHelper.clampedLerp(0, maxAlpha, (float) currentTime / markData.fadeIn());
        }
        return alpha;
    }

    public record CrosshairMarkData(int fadeIn, int hold, int fadeOut, int color) {
        public int getTotalTime() {
            return fadeIn + hold + fadeOut;
        }
    }
}
