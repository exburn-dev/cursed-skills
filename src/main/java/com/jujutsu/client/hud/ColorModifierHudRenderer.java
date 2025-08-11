package com.jujutsu.client.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

public class ColorModifierHudRenderer {
    private static ColorModifierData colorModifier = null;

    private static int currentTime = 0;
    private static float prevTickDelta = 0;

    public static void addColorModifier(ColorModifierData colorModifier) {
        ColorModifierHudRenderer.colorModifier = colorModifier;
    }

    public static void render(DrawContext context, RenderTickCounter counter) {
        if(colorModifier == null) return;

        if(currentTime >= colorModifier.getTotalTime()) {
            colorModifier = null;
            currentTime = 0;
            return;
        }

        float colorStrength = getValue(colorModifier, colorModifier.maxStrength());
        float brightness = getValue(colorModifier, colorModifier.maxBrightness());
        float r = ColorHelper.Argb.getRed(colorModifier.color()) / 255f;
        float g = ColorHelper.Argb.getGreen(colorModifier.color()) / 255f;
        float b = ColorHelper.Argb.getBlue(colorModifier.color()) / 255f;
        ShaderUtils.renderColorModifier(colorStrength, brightness, r, g, b);

        if(prevTickDelta > counter.getTickDelta(true)) {
            currentTime++;
        }
        prevTickDelta = counter.getTickDelta(true);
    }

    private static float getValue(ColorModifierData colorModifier, float maxValue) {
        int totalTime = colorModifier.getTotalTime();
        float alpha;

        if(currentTime >= totalTime - colorModifier.fadeOut()) {
            alpha = MathHelper.clampedLerp(maxValue, 0, (float) (currentTime - totalTime + colorModifier.fadeOut()) / 20);
        }
        else {
            alpha = MathHelper.clampedLerp(0, maxValue, (float) currentTime / colorModifier.fadeIn());
        }
        return alpha;
    }

    public record ColorModifierData(int fadeIn, int hold, int fadeOut, float maxStrength, float maxBrightness, int color) {
        public int getTotalTime() {
            return fadeIn + hold + fadeOut;
        }
    }
}
