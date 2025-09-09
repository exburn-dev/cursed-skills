package com.jujutsu.systems.ability.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;

public record AbilityAttributeModifier(double value, Type type) {
    public static final Codec<AbilityAttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("value").forGetter(AbilityAttributeModifier::value),
            Type.CODEC.fieldOf("type").forGetter(AbilityAttributeModifier::type)
    ).apply(instance, AbilityAttributeModifier::new));

    public static final PacketCodec<RegistryByteBuf, AbilityAttributeModifier> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.DOUBLE, AbilityAttributeModifier::value,
            Type.PACKET_CODEC, AbilityAttributeModifier::type,
            AbilityAttributeModifier::new);

    public double applyToValue(double value) {
        if(type == Type.MULTIPLY) {
            return value * this.value();
        }
        else {
            return value + this.value();
        }
    }

    public enum Type implements StringIdentifiable {
        ADD("add", 0),
        MULTIPLY("multiply", 1);

        public static final Codec<Type> CODEC;
        public static final PacketCodec<RegistryByteBuf, Type> PACKET_CODEC;

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
        public String asString() {
            return name;
        }

        static {
            CODEC = StringIdentifiable.createCodec(Type::values);
            PACKET_CODEC = PacketCodecs.indexed(Type::byId, Type::getId).cast();
        }
    }
}
