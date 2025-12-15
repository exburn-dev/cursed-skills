package com.jujutsu.network.payload;

import com.jujutsu.network.ModNetworkConstants;
import com.jujutsu.network.NbtPacketCodec;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.jujutsu.systems.ability.data.AbilityPropertiesContainer;
import com.jujutsu.systems.ability.data.AbilityProperty;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.HashMap;
import java.util.Map;

public class AbilityRuntimeDataSyncS2CPacket {
    public static final CustomPayload.Id<Payload> ID = new CustomPayload.Id<>(ModNetworkConstants.SYNC_RUNTIME_DATA_ID);
    public static final PacketCodec<RegistryByteBuf, Payload> CODEC = PacketCodec.tuple(
            AbilitySlot.PACKET_CODEC, Payload::slot,
            AbilityPropertiesContainer.PACKET_CODEC_MAP, Payload::data,
            Payload::new
    );

    public static void register() {

    }

    public record Payload(AbilitySlot slot, Map<AbilityProperty<?>, Comparable<?>> data) implements CustomPayload {
        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
