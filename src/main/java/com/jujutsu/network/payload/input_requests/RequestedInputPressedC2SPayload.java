package com.jujutsu.network.payload.input_requests;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.ability.core.AbilityComponent;
import com.jujutsu.systems.ability.core.AbilitySlot;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RequestedInputPressedC2SPayload(AbilitySlot slot) implements CustomPayload {
    public static final Identifier PACKET_ID = Jujutsu.id("requested_input_pressed");
    public static final Id<RequestedInputPressedC2SPayload> ID = new Id<>(PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, RequestedInputPressedC2SPayload> CODEC = PacketCodec.tuple(
            AbilitySlot.PACKET_CODEC, RequestedInputPressedC2SPayload::slot, RequestedInputPressedC2SPayload::new);

    public static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
            AbilityComponent component = AbilityComponent.get(context.player());
            component.receiveRequestedInputPressed(payload.slot());
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
