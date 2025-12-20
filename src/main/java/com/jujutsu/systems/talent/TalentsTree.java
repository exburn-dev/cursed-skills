package com.jujutsu.systems.talent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.List;

public record TalentsTree(Identifier id, List<Identifier> branches) {
    public static final Codec<TalentsTree> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(TalentsTree::id),
            Identifier.CODEC.listOf().fieldOf("branches").forGetter(TalentsTree::branches)
    ).apply(instance, TalentsTree::new));
}
