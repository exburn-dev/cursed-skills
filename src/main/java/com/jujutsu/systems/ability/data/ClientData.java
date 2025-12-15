package com.jujutsu.systems.ability.data;

import com.jujutsu.systems.ability.core.AbilityInstanceOld;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

public record ClientData(@Nullable AbilityHandAnimation animation, @Nullable AbilityHudOverlay overlay) {
    public static class Builder {
        private AbilityHandAnimation animation = null;
        private AbilityHudOverlay overlay = null;

        public Builder addAnimation(AbilityHandAnimation animation) {
            this.animation = animation;
            return this;
        }

        public Builder addOverlay(AbilityHudOverlay overlay) {
            this.overlay = overlay;
            return this;
        }

        public ClientData build() {
            return new ClientData(this.animation, this.overlay);
        }
    }

    @FunctionalInterface
    public interface AbilityHandAnimation {
        boolean render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, AbilityInstanceOld instance, ClientPlayerEntity player, PlayerEntityRenderer playerEntityRenderer, float equipProgress, float swingProgress, int light);
    }

    @FunctionalInterface
    public interface AbilityHudOverlay {
        void render(DrawContext context, RenderTickCounter counter, AbilityInstanceOld instance);
    }
}
