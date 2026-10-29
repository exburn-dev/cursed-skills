package com.jujutsu.api.ability.upgrade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.*;

public record TalentsData(Identifier tree, int points, Map<Identifier, Identifier> purchasedUpgrades, Identifier lastPurchasedBranch) {
    public static final Codec<TalentsData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("tree").forGetter(TalentsData::tree),
            Codec.INT.fieldOf("points").forGetter(TalentsData::points),
            Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC).fieldOf("purchasedUpgrades").forGetter(TalentsData::purchasedUpgrades),
            Identifier.CODEC.fieldOf("lastPurchasedBranch").forGetter(TalentsData::lastPurchasedBranch)
    ).apply(instance, TalentsData::new));

    public static final StreamCodec<FriendlyByteBuf, TalentsData> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, TalentsData::tree,
            ByteBufCodecs.INT, TalentsData::points,
            ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, Identifier.STREAM_CODEC), TalentsData::purchasedUpgrades,
            Identifier.STREAM_CODEC, TalentsData::lastPurchasedBranch,
            TalentsData::new);
}
