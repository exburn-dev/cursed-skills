package com.jujutsu.network.payload.sounds;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.sound.HollowPurpleChargingSoundInstance;
import com.jujutsu.systems.ability.core.AbilitySlot;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public record PlayAbilityChargingSoundS2CPayload(SoundEvent sound, AbilitySlot slot) implements CustomPayload {
    public static final Identifier PACKET_ID = Jujutsu.id("play_ability_charging_sound");
    public static final Id<PlayAbilityChargingSoundS2CPayload> ID = new Id<>(PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, PlayAbilityChargingSoundS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.registryCodec(Registries.SOUND_EVENT.getCodec()), PlayAbilityChargingSoundS2CPayload::sound,
            AbilitySlot.PACKET_CODEC, PlayAbilityChargingSoundS2CPayload::slot,
            PlayAbilityChargingSoundS2CPayload::new
    );

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.getSoundManager().play(new HollowPurpleChargingSoundInstance(
                    client.player,
                    payload.slot,
                    payload.sound,
                    SoundCategory.MASTER
            ));
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
