package com.jujutsu.api.talent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.List;

public record TalentBranch(Identifier id, List<Identifier> talents) {
    public static final Codec<TalentBranch> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(TalentBranch::id),
            Identifier.CODEC.listOf().fieldOf("talents").forGetter(TalentBranch::talents)
    ).apply(instance, TalentBranch::new));

    public static final StreamCodec<FriendlyByteBuf, TalentBranch> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, TalentBranch::id,
            Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), TalentBranch::talents,
            TalentBranch::new
    );
}
