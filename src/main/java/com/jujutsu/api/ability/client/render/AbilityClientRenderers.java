package com.jujutsu.api.ability.client.render;

import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class AbilityClientRenderers {
    public final AbilityHudOverlay hudOverlay;
    public final AbilityHandAnimation handAnimation;

    public AbilityClientRenderers(@Nullable AbilityHudOverlay hudOverlay, @Nullable AbilityHandAnimation handAnimation) {
        this.hudOverlay = hudOverlay;
        this.handAnimation = handAnimation;
    }

    public boolean hasOverlay() {
        return hudOverlay != null;
    }

    public boolean hasHandAnimation() {
        return handAnimation != null;
    }

    @Nullable
    public AbilityHudOverlay overlay() {
        return hudOverlay;
    }

    @Nullable
    public AbilityHandAnimation handAnimation() {
        return handAnimation;
    }

    public Optional<AbilityHudOverlay> optionalOverlay() {
        return Optional.ofNullable(hudOverlay);
    }

    public Optional<AbilityHandAnimation> optionalHandAnimation() {
        return Optional.ofNullable(handAnimation);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private AbilityHudOverlay hud = null;
        private AbilityHandAnimation handAnim = null;

        public Builder addHudOverlay(AbilityHudOverlay overlay) {
            this.hud = overlay;
            return this;
        }

        public Builder addHandAnimation(AbilityHandAnimation animation) {
            this.handAnim = animation;
            return this;
        }

        public AbilityClientRenderers build() {
            return new AbilityClientRenderers(hud, handAnim);
        }
    }
}
