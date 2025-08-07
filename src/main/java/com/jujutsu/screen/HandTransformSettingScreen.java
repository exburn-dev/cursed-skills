package com.jujutsu.screen;

import com.google.common.collect.Lists;
import com.jujutsu.Jujutsu;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.JigsawBlockScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class HandTransformSettingScreen extends Screen {
    public static double TRANSLATE_X = 0;
    public static double TRANSLATE_Y = 0;
    public static double TRANSLATE_Z = 0;
    public static double ROTATE_X = 0;
    public static double ROTATE_Y = 0;
    public static double ROTATE_Z = 0;
    public static boolean BEFORE = false;

    public HandTransformSettingScreen(boolean before) {
        super(Text.literal("Hand transform"));
        BEFORE = before;
        TRANSLATE_X = 0;
        TRANSLATE_Y = 0;
        TRANSLATE_Z = 0;
        ROTATE_X = 0;
        ROTATE_Y = 0;
        ROTATE_Z = 0;
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(new SliderWidget(100, 50, 100, 25, Text.literal("translate x"), 0.5) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("translate x").append(" " + String.format("%.1f", this.value)));
            }

            @Override
            protected void applyValue() {
                TRANSLATE_X = MathHelper.clampedLerp(-1, 1, this.value);
            }
        });
        this.addDrawableChild(new SliderWidget(100, 95, 100, 25, Text.literal("translate y"), 0.5) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("translate y").append(" " + String.format("%.1f", this.value)));
            }

            @Override
            protected void applyValue() {
                TRANSLATE_Y = MathHelper.clampedLerp(-1, 1, this.value);
            }
        });
        this.addDrawableChild(new SliderWidget(100, 95 + 45, 100, 25, Text.literal("translate z"), 0.5) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("translate z").append(" " + String.format("%.1f", this.value)));
            }

            @Override
            protected void applyValue() {
                TRANSLATE_Z = MathHelper.clampedLerp(-1, 1, this.value);
            }
        });

        this.addDrawableChild(new SliderWidget(100, 95 + 45 + 45, 100, 25, Text.literal("rotate x"), 0.5) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("rotate x").append(" " + MathHelper.floor(MathHelper.clampedLerp(-90.0, 90.0, this.value))));
            }

            @Override
            protected void applyValue() {
                ROTATE_X = MathHelper.clampedLerp(-90.0, 90.0, this.value);
            }
        });

        this.addDrawableChild(new SliderWidget(100, 95 + 45 + 45 + 45, 100, 25, Text.literal("rotate Y"), 0.5) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("rotate Y").append(" " + MathHelper.floor(MathHelper.clampedLerp(-90.0, 90.0, this.value))));
            }

            @Override
            protected void applyValue() {
                ROTATE_Y = MathHelper.clampedLerp(-90.0, 90.0, this.value);
            }
        });

        this.addDrawableChild(new SliderWidget(100, 95 + 45 + 45 + 45 + 45, 100, 25, Text.literal("rotate z"), 0.5) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("rotate z").append(" " + MathHelper.floor(MathHelper.clampedLerp(-90.0, 90.0, this.value))));
            }

            @Override
            protected void applyValue() {
                ROTATE_Z = MathHelper.clampedLerp(-90.0, 90.0, this.value);
            }
        });

        this.addDrawableChild(new ButtonWidget.Builder(Text.literal("save"), (button -> {
            Jujutsu.LOGGER.info("translation: [{}f, {}f, {}f]\n rotation: [{}f, {}f, {}f]", TRANSLATE_X, TRANSLATE_Y, TRANSLATE_Z, ROTATE_X, ROTATE_Y, ROTATE_Z);
        })).dimensions(width / 2 - 50, height - 40, 100, 30).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public static class HandTransformSlider extends SliderWidget {
        private final Consumer<Double> applyValue;

        public HandTransformSlider(int x, int y, int width, int height, Text text, double value, Consumer<Double> applyValue) {
            super(x, y, width, height, text, value);
            this.applyValue = applyValue;
        }

        @Override
        protected void updateMessage() {}

        @Override
        protected void applyValue() {
            this.applyValue.accept(value);
        }
    }
}
