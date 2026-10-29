package com.jujutsu.network.payload.input_requests;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.input.InputRequestSystem;
import com.jujutsu.api.ability.core.AbilitySlot;
import com.jujutsu.api.ability.data.RequestedInputKey;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record RequestInputS2CPayload(RequestedInputKey key, AbilitySlot slot) implements CustomPacketPayload {
    public static final Identifier ID = Jujutsu.id("request_input");
    public static final Type<RequestInputS2CPayload> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, RequestInputS2CPayload> CODEC = StreamCodec.composite(
            RequestedInputKey.PACKET_CODEC, RequestInputS2CPayload::key,
            AbilitySlot.STREAM_CODEC, RequestInputS2CPayload::slot,
            RequestInputS2CPayload::new);

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> {
            InputRequestSystem.addInputRequest(payload.slot(), payload.key);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
