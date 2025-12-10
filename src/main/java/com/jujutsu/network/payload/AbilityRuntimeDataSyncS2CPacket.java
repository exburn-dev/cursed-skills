package com.jujutsu.network.payload;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.ability.data.AbilityProperty;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Map;

public class AbilityRuntimeDataSyncS2CPacket {
    public static final Identifier PACKET_ID = Jujutsu.getId("ability_runtime_data_sync");
    public static final CustomPayload.Id<Payload> PAYLOAD_ID = new CustomPayload.Id<>(PACKET_ID);

    public record Payload(Map<AbilityProperty<?>, Comparable<?>> data) implements CustomPayload {
        @Override
        public Id<? extends CustomPayload> getId() {
            return PAYLOAD_ID;
        }
    }

    //public static class
}
