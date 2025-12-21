package com.jujutsu.systems.talent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

import java.util.List;

public record TalentBranch(Identifier id, List<Identifier> talents) {
    public static final Codec<TalentBranch> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(TalentBranch::id),
            Identifier.CODEC.listOf().fieldOf("talents").forGetter(TalentBranch::talents)
    ).apply(instance, TalentBranch::new));

    public static final PacketCodec<RegistryByteBuf, TalentBranch> PACKET_CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, TalentBranch::id,
            Identifier.PACKET_CODEC.collect(PacketCodecs.toList()), TalentBranch::talents,
            TalentBranch::new
    );
}
