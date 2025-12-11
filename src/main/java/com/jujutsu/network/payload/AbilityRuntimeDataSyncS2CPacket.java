package com.jujutsu.network.payload;

import com.jujutsu.Jujutsu;
import com.jujutsu.network.NbtPacketCodec;
import com.jujutsu.systems.ability.data.AbilityProperty;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class AbilityRuntimeDataSyncS2CPacket {
    public static final Identifier PACKET_ID = Jujutsu.getId("ability_runtime_data_sync");
    public static final CustomPayload.Id<Payload> PAYLOAD_ID = new CustomPayload.Id<>(PACKET_ID);
    public static final Codec<Payload> PAYLOAD_CODEC = (new PayloadCodec()).xmap(Payload::new, Payload::data);
    public static final PacketCodec<RegistryByteBuf, Payload> CODEC = new NbtPacketCodec<>(PAYLOAD_CODEC);

    public record Payload(Map<AbilityProperty<?>, Comparable<?>> data) implements CustomPayload {
        @Override
        public Id<? extends CustomPayload> getId() {
            return PAYLOAD_ID;
        }
    }

    public static class PayloadCodec implements Codec<Map<AbilityProperty<?>, Comparable<?>>> {

        @Override
        public <T> DataResult<Pair<Map<AbilityProperty<?>, Comparable<?>>, T>> decode(DynamicOps<T> ops, T t) {
            Dynamic<T> dyn = new Dynamic<>(ops, t);

            var map = dyn.getMapValues().getOrThrow();
            Map<AbilityProperty<?>, Comparable<?>> resultMap = new HashMap<>();

            for(var entry : map.entrySet()) {
                String name = entry.getKey().asString("");
                String type = entry.getValue().get("type").asString("");
                AbilityProperty<?> prop = AbilityProperty.fromType(name, type);

                Codec<Comparable<?>> codec = (Codec<Comparable<?>>) prop.getCodec();
                Dynamic<T> valueDyn = entry.getValue().get("value").get().getOrThrow();

                Comparable<?> value = codec.parse(valueDyn).getOrThrow();
                resultMap.put(prop, value);
            }

            return DataResult.success(new Pair<>(resultMap, t));
        }

        @Override
        public <T> DataResult<T> encode(Map<AbilityProperty<?>, Comparable<?>> map, DynamicOps<T> ops, T t) {
            RecordBuilder<T> builder = ops.mapBuilder();

            for(var entry : map.entrySet()) {
                AbilityProperty<?> prop = entry.getKey();
                String name = prop.name();
                String type = prop.type();

                Codec<Comparable<?>> codec = (Codec<Comparable<?>>) prop.getCodec();
                T value = codec.encodeStart(ops, entry.getValue()).getOrThrow();

                RecordBuilder<T> valueBuilder = ops.mapBuilder();
                valueBuilder.add("type", ops.createString(type));
                valueBuilder.add("value", value);

                T entryValue = valueBuilder.build(ops.empty()).getOrThrow();

                builder.add(name, entryValue);
            }

            return builder.build(t);
        }
    }
}
