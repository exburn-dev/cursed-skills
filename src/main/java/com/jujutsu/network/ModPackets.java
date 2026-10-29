package com.jujutsu.network;

import com.jujutsu.network.payload.ShowCrosshairMarkPayload;
import com.jujutsu.network.payload.ShowScreenColorModifierPayload;
import com.jujutsu.network.payload.ShowScreenFlashPayload;
import com.jujutsu.network.payload.input_requests.RequestInputS2CPayload;
import com.jujutsu.network.payload.input_requests.RequestedInputPressedC2SPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class ModPackets {
    public static void registerPackets() {
        registerClientbound(PayloadTypeRegistry.playS2C());
        registerClientboundReceivers();

        registerServerbound(PayloadTypeRegistry.playC2S());
        registerServerboundReceivers();
    }

    public static void registerClientbound(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
        registry.register(ShowScreenFlashPayload.TYPE, ShowScreenFlashPayload.CODEC);
        registry.register(ShowCrosshairMarkPayload.TYPE, ShowCrosshairMarkPayload.CODEC);
        registry.register(ShowScreenColorModifierPayload.TYPE, ShowScreenColorModifierPayload.CODEC);

        registry.register(RequestInputS2CPayload.TYPE, RequestInputS2CPayload.CODEC);

    }

    public static void registerClientboundReceivers() {
        ShowScreenFlashPayload.registerClientReceiver();
        ShowCrosshairMarkPayload.registerClientReceiver();
        ShowScreenColorModifierPayload.registerClientReceiver();

        RequestInputS2CPayload.registerClientReceiver();
    }

    public static void registerServerbound(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
        registry.register(RequestedInputPressedC2SPayload.TYPE, RequestedInputPressedC2SPayload.CODEC);
    }

    public static void registerServerboundReceivers() {
        RequestedInputPressedC2SPayload.registerServerReceiver();
    }
}
