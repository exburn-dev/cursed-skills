package com.jujutsu.systems.ability.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

public record AbilityAttributeModifier(Identifier id, double value, Type type) {
    public static final Codec<AbilityAttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(AbilityAttributeModifier::id),
            Codec.DOUBLE.fieldOf("value").forGetter(AbilityAttributeModifier::value),
            Type.CODEC.fieldOf("type").forGetter(AbilityAttributeModifier::type)
    ).apply(instance, AbilityAttributeModifier::new));

    public static final PacketCodec<RegistryByteBuf, AbilityAttributeModifier> PACKET_CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, AbilityAttributeModifier::id,
            PacketCodecs.DOUBLE, AbilityAttributeModifier::value,
            Type.PACKET_CODEC, AbilityAttributeModifier::type,
            AbilityAttributeModifier::new);

    public enum Type {
        ADD(0),
        MULTIPLY(1);

        public static final Codec<Type> CODEC;
        public static final PacketCodec<RegistryByteBuf, Type> PACKET_CODEC;

        private final int id;

        Type(int id) {
            this.id = id;
        }

        public static Type byId(int id) {
            return Type.values()[id];
        }

        static {
            CODEC = Codec.INT.xmap(Type::byId, type -> type.id);
            PACKET_CODEC = PacketCodec.of(
                    (type, buf) -> buf.writeVarInt(type.id),
                    (buf) -> Type.byId(buf.readVarInt()));
        }
    }
}
