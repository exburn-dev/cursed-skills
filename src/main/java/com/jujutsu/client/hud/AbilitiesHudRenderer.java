package com.jujutsu.client.hud;

import com.jujutsu.systems.ability.AbilityInstance;
import com.jujutsu.systems.ability.AbilitySlot;
import com.jujutsu.systems.ability.ClientData;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;

public class AbilitiesHudRenderer {
    public static void render(DrawContext context, RenderTickCounter counter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client == null || client.player == null) return;

        IAbilitiesHolder holder = (IAbilitiesHolder) client.player;

        MatrixStack matrices = context.getMatrices();
        matrices.push();

        for(AbilitySlot slot: holder.getRunningSlots()) {
            AbilityInstance instance = holder.getAbilityInstance(slot);
            if(!instance.getStatus().isRunning()) continue;

            ClientData clientData = instance.getType().getClientData();
            if(clientData != null && clientData.overlay() != null) {
                clientData.overlay().render(context, counter, instance);
            }
        }
        matrices.pop();
    }
}
