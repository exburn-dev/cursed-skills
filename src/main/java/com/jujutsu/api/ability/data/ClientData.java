package com.jujutsu.api.ability.data;

import com.jujutsu.api.ability.core.AbilityInstance;
import com.jujutsu.api.ability.core.AbilityInstanceData;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.Nullable;

@Deprecated
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

    @Deprecated
    @FunctionalInterface
    public interface AbilityHandAnimation {
        void render();
        //boolean render(MatrixStack matrices, MultiBufferSource bufferSource, AbilityInstance clientInstance, ClientPlayerEntity player, PlayerEntityRenderer playerEntityRenderer, float equipProgress, float swingProgress, int light);
    }

    @Deprecated
    @FunctionalInterface
    public interface AbilityHudOverlay {
        void render();
        //void render(DrawContext context, RenderTickCounter counter, AbilityInstanceData instance);
    }
}
