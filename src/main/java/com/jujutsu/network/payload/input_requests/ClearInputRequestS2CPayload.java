package com.jujutsu.network.payload.input_requests;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.keybind.InputRequestSystem;
import com.jujutsu.systems.ability.core.AbilitySlot;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ClearInputRequestS2CPayload(AbilitySlot slot) implements CustomPayload {
    public static final Identifier PACKET_ID = Jujutsu.id("clear_input_request");
    public static final Id<ClearInputRequestS2CPayload> ID = new Id<>(PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, ClearInputRequestS2CPayload> CODEC = PacketCodec.tuple(
            AbilitySlot.PACKET_CODEC, ClearInputRequestS2CPayload::slot, ClearInputRequestS2CPayload::new
    );

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
            InputRequestSystem.clearSlot(payload.slot);
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
