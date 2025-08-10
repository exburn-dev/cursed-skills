package com.jujutsu.client.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class FlashSystemHudRenderer {
    private static final List<FlashData> flashDataQueue = new ArrayList<>();
    private static int currentTime = 0;
    private static float prevTickDelta = 0;
    
    public static void addFlashData(FlashData flashData) {
        flashDataQueue.add(flashData);
    }

    public static void render(DrawContext context, RenderTickCounter counter) {
        if(flashDataQueue.isEmpty()) return;

        FlashData flashData = flashDataQueue.getFirst();

        if(currentTime >= flashData.getTotalTime()) {
            flashDataQueue.removeFirst();
            currentTime = 0;
            return;
        }

        float alpha = getAlpha(flashData);
        ShaderUtils.renderVignette(alpha, ColorHelper.Argb.getRed(flashData.color()) / 255f, ColorHelper.Argb.getGreen(flashData.color()) / 255f, ColorHelper.Argb.getBlue(flashData.color()) / 255f);

        if(prevTickDelta > counter.getTickDelta(true)) {
            currentTime++;
        }
        prevTickDelta = counter.getTickDelta(true);
    }

    private static float getAlpha(FlashData flashData) {
        int totalTime = flashData.getTotalTime();
        float maxAlpha = flashData.maxAlpha();
        float alpha;

        if(currentTime >= totalTime - flashData.fadeOut()) {
            alpha = MathHelper.clampedLerp(maxAlpha, 0, (float) (currentTime - totalTime + flashData.fadeOut()) / 20);
        }
        else {
            alpha = MathHelper.clampedLerp(0, maxAlpha, (float) currentTime / flashData.fadeIn());
        }
        return alpha;
    }

    public record FlashData(int fadeIn, int hold, int fadeOut, float maxAlpha, int color) {
        public int getTotalTime() {
            return fadeIn + hold + fadeOut;
        }
    }
}
