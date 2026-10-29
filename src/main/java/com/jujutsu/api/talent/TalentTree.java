package com.jujutsu.api.talent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.List;

public record TalentTree(Identifier id, List<Identifier> branches) {
    public static final Codec<TalentTree> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(TalentTree::id),
            Identifier.CODEC.listOf().fieldOf("branches").forGetter(TalentTree::branches)
    ).apply(instance, TalentTree::new));

    public static final StreamCodec<FriendlyByteBuf, TalentTree> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, TalentTree::id,
            Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), TalentTree::branches,
            TalentTree::new
    );

    public Identifier getNext(Identifier previous) {
        Identifier result = branches.getFirst();

        for(int i = 0; i < size(); i++) {
            Identifier branch = branches.get(i);
            if(branch.equals(previous) && i < size() - 1) {
                result = branches.get(i + 1);
                break;
            }
        }
        return result;
    }

    public int size() {
        return branches.size();
    }
}
