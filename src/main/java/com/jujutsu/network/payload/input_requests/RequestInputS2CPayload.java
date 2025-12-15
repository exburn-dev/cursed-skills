package com.jujutsu.network.payload.input_requests;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.keybind.InputRequestSystem;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.jujutsu.systems.ability.data.RequestedInputKey;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RequestInputS2CPayload(RequestedInputKey key, AbilitySlot slot) implements CustomPayload {
    public static final Identifier PACKET_ID = Jujutsu.id("request_input");
    public static final Id<RequestInputS2CPayload> ID = new Id<>(PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, RequestInputS2CPayload> CODEC = PacketCodec.tuple(
            RequestedInputKey.PACKET_CODEC, RequestInputS2CPayload::key,
            AbilitySlot.PACKET_CODEC, RequestInputS2CPayload::slot,
            RequestInputS2CPayload::new);

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
            InputRequestSystem.addInputRequest(payload.slot(), payload.key);
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
