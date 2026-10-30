package com.jujutsu.api.ability.client.render;

import com.jujutsu.api.ability.core.AbilityInstance;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

@FunctionalInterface
public interface AbilityHudOverlay {
    void render(GuiGraphics graphics, DeltaTracker tickCounter, AbilityInstance clientInstance);
}
