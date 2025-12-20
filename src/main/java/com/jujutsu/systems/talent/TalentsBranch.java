package com.jujutsu.systems.talent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.List;

public record TalentsBranch(Identifier id, List<Identifier> talents) {
    public static final Codec<TalentsBranch> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(TalentsBranch::id),
            Identifier.CODEC.listOf().fieldOf("talents").forGetter(TalentsBranch::talents)
    ).apply(instance, TalentsBranch::new));
}
