package com.jujutsu.api.ability.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;

public class AbilityPropertiesContainer {
    public static final Codec<AbilityPropertiesContainer> CODEC;
    public static final StreamCodec<ByteBuf, AbilityPropertiesContainer> STREAM_CODEC;

    private final Map<AbilityProperty<?>, Comparable<?>> properties;

    public AbilityPropertiesContainer() {
        properties = new HashMap<>();
    }

    public AbilityPropertiesContainer(Map<AbilityProperty<?>, Comparable<?>> properties ) {
        this.properties = properties;
    }

    public <T extends Comparable<T>> void set(AbilityProperty<T> property, T value) {
        properties.put(property, value);
    }

    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> T get(AbilityProperty<T> property) {
        return (T) properties.get(property);
    }

    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> T getOrDefault(AbilityProperty<T> property, T fallback) {
        return (T) properties.getOrDefault(property, fallback);
    }

    public Map<AbilityProperty<?>, Comparable<?>> properties() {
        return properties;
    }

    static {
        CODEC = new RuntimeDataCodec();
        STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
    }

    public static class RuntimeDataCodec implements Codec<AbilityPropertiesContainer> {
        @Override
        public <T> DataResult<Pair<AbilityPropertiesContainer, T>> decode(DynamicOps<T> ops, T t) {
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

            return DataResult.success(new Pair<>(new AbilityPropertiesContainer(resultMap), t));
        }

        @Override
        public <T> DataResult<T> encode(AbilityPropertiesContainer container, DynamicOps<T> ops, T t) {
            RecordBuilder<T> builder = ops.mapBuilder();

            for(var entry : container.properties.entrySet()) {
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
