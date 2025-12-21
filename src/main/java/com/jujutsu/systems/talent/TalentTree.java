package com.jujutsu.systems.talent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

import java.util.List;

public record TalentTree(Identifier id, List<Identifier> branches) {
    public static final Codec<TalentTree> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(TalentTree::id),
            Identifier.CODEC.listOf().fieldOf("branches").forGetter(TalentTree::branches)
    ).apply(instance, TalentTree::new));

    public static final PacketCodec<RegistryByteBuf, TalentTree> PACKET_CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, TalentTree::id,
            Identifier.PACKET_CODEC.collect(PacketCodecs.toList()), TalentTree::branches,
            TalentTree::new
    );

    public int size() {
        return branches.size();
    }
}
