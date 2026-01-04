package com.jujutsu.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

public class SkillsText {

    public static MutableText createGradient(String text, int startColor, int endColor) {
        MutableText result = Text.empty();
        int size = text.replaceAll("\\s", "").length();

        int r1 = startColor >> 16 & 255;
        int g1 = startColor >> 8 & 255;
        int b1 = startColor & 255;

        int r2 = endColor >> 16 & 255;
        int g2 = endColor >> 8 & 255;
        int b2 = endColor & 255;

        for(int i = 0; i < size; i++) {
            String currentChar = Character.toString(text.charAt(i));
            float fraction = i / (size - 1f);

            int r = (int) (r1 + (r2 - r1) * fraction);
            int g = (int) (g1 + (g2 - g1) * fraction);
            int b = (int) (b1 + (b2 - b1) * fraction);

            result.append(Text.literal(currentChar).withColor(ColorHelper.Argb.getArgb(r, g, b)));
        }
        return result;
    }
}
