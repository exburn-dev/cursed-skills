package com.jujutsu.systems.buff;

import com.jujutsu.network.NbtPacketCodec;
import com.jujutsu.systems.buff.type.AttributeBuff;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record BuffData(List<BuffPredicate> conditions, boolean waitAllConditions, AttributeBuff provider) {
    public static final Codec<BuffData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuffPredicate.CODEC.listOf().fieldOf("conditions").forGetter(BuffData::conditions),
            Codec.BOOL.fieldOf("waitAllConditions").forGetter(BuffData::waitAllConditions),
            AttributeBuff.CODEC.fieldOf("provider").forGetter(BuffData::provider)
            ).apply(instance, BuffData::new)
        );

    public static final PacketCodec<RegistryByteBuf, BuffData> PACKET_CODEC = new NbtPacketCodec<>(CODEC);
}
