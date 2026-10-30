package com.jujutsu.api.ability.client.render;

import com.jujutsu.api.ability.core.AbilityInstance;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import org.joml.Matrix3x2fStack;

@FunctionalInterface
public interface AbilityHandAnimation {
    boolean render(Matrix3x2fStack matrices, MultiBufferSource bufferSource, AbilityInstance clientInstance,
                   LocalPlayer player, AvatarRenderer<LocalPlayer> playerRenderer,
                   float equipProgress, float swingProgress, int light);
}
