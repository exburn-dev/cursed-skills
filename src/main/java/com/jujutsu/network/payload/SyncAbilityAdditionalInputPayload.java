package com.jujutsu.network.payload;

import com.jujutsu.client.keybind.AdditionalInputSystem;
import com.jujutsu.network.ModNetworkConstants;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.jujutsu.systems.ability.data.AbilityAdditionalInput;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.List;

public record SyncAbilityAdditionalInputPayload(List<AbilityAdditionalInput> additionalInput, AbilitySlot slot, boolean clear) implements CustomPayload {
    public static final Id<SyncAbilityAdditionalInputPayload> ID = new Id<>(ModNetworkConstants.SYNC_ABILITY_ADDITIONAL_INPUT_ID);
    public static final PacketCodec<RegistryByteBuf, SyncAbilityAdditionalInputPayload> CODEC = PacketCodec.tuple(
            AbilityAdditionalInput.PACKET_CODEC.collect(PacketCodecs.toList()), SyncAbilityAdditionalInputPayload::additionalInput,
            AbilitySlot.PACKET_CODEC, SyncAbilityAdditionalInputPayload::slot,
            PacketCodecs.BOOL, SyncAbilityAdditionalInputPayload::clear,
            SyncAbilityAdditionalInputPayload::new);

    public static void receiveOnClient(SyncAbilityAdditionalInputPayload payload, ClientPlayNetworking.Context context) {
        if(payload.clear()) {
            AdditionalInputSystem.clearSlot(payload.slot);
        }
        else {
            for(AbilityAdditionalInput input: payload.additionalInput()) {
                AdditionalInputSystem.addAdditionalInput(payload.slot(), input);
            }
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
