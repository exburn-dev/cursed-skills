package com.jujutsu.item;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface IBorderTooltipItem {
    TooltipBorderData getBorderData();
    int getOffset();
    default void render(DrawContext context, int x, int y, int width, int height) {}

    record TooltipBorderData(@Nullable Identifier mainBorder, @Nullable Identifier upperTile, @Nullable Identifier upperDecorTile,
                             @Nullable Identifier bottomTile, @Nullable Identifier bottomDecorTile,
                             @Nullable BorderCentralElement upperCentralElement, @Nullable BorderCentralElement bottomCentralElement) {
        public static class Builder {
            private Identifier mainBorder;
            private Identifier upperTile = null;
            private Identifier upperDecorTile = null;
            private Identifier bottomTile = null;
            private Identifier bottomDecorTile = null;
            private BorderCentralElement upperCentralElement = null;
            private BorderCentralElement bottomCentralElement = null;

            public Builder() {
                this(null);
            }

            public Builder(Identifier mainBorder) {
                this.mainBorder = mainBorder;
            }

            public Builder addMainBorder(Identifier id) {
                this.mainBorder = id;
                return this;
            }

            public Builder addUpperTile(Identifier id) {
                this.upperTile = id;
                return this;
            }

            public Builder addUpperDecorTile(Identifier id) {
                this.upperDecorTile = id;
                return this;
            }

            public Builder addBottomTile(Identifier id) {
                this.bottomTile = id;
                return this;
            }

            public Builder addBottomDecorTile(Identifier id) {
                this.bottomDecorTile = id;
                return this;
            }

            public Builder addUpperCentralElement(Identifier texture, int width, int height, float scale) {
                this.upperCentralElement = new BorderCentralElement(texture, width, height, scale);
                return this;
            }

            public Builder addBottomCentralElement(Identifier texture, int width, int height, float scale) {
                this.bottomCentralElement = new BorderCentralElement(texture, width, height, scale);
                return this;
            }

            public TooltipBorderData build() {
                return new TooltipBorderData(this.mainBorder, this.upperTile, this.upperDecorTile, this.bottomTile, this.bottomDecorTile, upperCentralElement, bottomCentralElement);
            }
        }
    }

    record BorderCentralElement(Identifier texture, int width, int height, float scale) {}
}
