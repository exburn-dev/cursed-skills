package com.jujutsu.api.ability.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public record AbilityAttributeModifier(double value, Type type) {
    public static final Codec<AbilityAttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("value").forGetter(AbilityAttributeModifier::value),
            Type.CODEC.fieldOf("type").forGetter(AbilityAttributeModifier::type)
    ).apply(instance, AbilityAttributeModifier::new));

    public static final StreamCodec<FriendlyByteBuf, AbilityAttributeModifier> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, AbilityAttributeModifier::value,
            Type.STREAM_CODEC, AbilityAttributeModifier::type,
            AbilityAttributeModifier::new);

    public enum Type implements StringRepresentable {
        ADD("add", 0),
        MULTIPLY("multiply", 1);

        public static final Codec<Type> CODEC;
        public static final StreamCodec<ByteBuf, Type> STREAM_CODEC;

        private final String name;
        private final int id;

        Type(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public static Type byId(int id) {
            return Type.values()[id];
        }

        public int getId() {
            return id;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        static {
            CODEC = StringRepresentable.fromEnum(Type::values);
            STREAM_CODEC = ByteBufCodecs.INT.map(Type::byId, Type::getId);
        }
    }
}
