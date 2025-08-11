package com.jujutsu.util;

import com.jujutsu.client.hud.CrosshairMarkRenderer;
import com.jujutsu.client.hud.FlashSystemHudRenderer;
import com.jujutsu.network.payload.ShowCrosshairMarkPayload;
import com.jujutsu.network.payload.ShowScreenFlashPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class VisualEffectUtils {
    public static void sendScreenFlash(ServerPlayerEntity player, int fadeIn, int hold, int fadeOut, float maxAlpha, int color) {
        ServerPlayNetworking.send(player, new ShowScreenFlashPayload(new FlashSystemHudRenderer.FlashData(fadeIn, hold, fadeOut, maxAlpha, color)));
    }

    public static void sendCrosshairMarkData(ServerPlayerEntity player, int fadeIn, int hold, int fadeOut, int color) {
        ServerPlayNetworking.send(player, new ShowCrosshairMarkPayload(new CrosshairMarkRenderer.CrosshairMarkData(fadeIn, hold, fadeOut, color)));
    }
}
