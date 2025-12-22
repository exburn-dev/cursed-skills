package com.jujutsu.systems.ability.upgrade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

import java.util.*;

public record TalentsData(Identifier tree, int points, Map<Identifier, Identifier> purchasedUpgrades, Identifier lastPurchasedBranch) {
    public static final Codec<TalentsData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("tree").forGetter(TalentsData::tree),
            Codec.INT.fieldOf("points").forGetter(TalentsData::points),
            Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC).fieldOf("purchasedUpgrades").forGetter(TalentsData::purchasedUpgrades),
            Identifier.CODEC.fieldOf("lastPurchasedBranch").forGetter(TalentsData::lastPurchasedBranch)
    ).apply(instance, TalentsData::new));

    public static final PacketCodec<RegistryByteBuf, TalentsData> PACKET_CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, TalentsData::tree,
            PacketCodecs.INTEGER, TalentsData::points,
            PacketCodecs.map(HashMap::new, Identifier.PACKET_CODEC, Identifier.PACKET_CODEC), TalentsData::purchasedUpgrades,
            Identifier.PACKET_CODEC, TalentsData::lastPurchasedBranch,
            TalentsData::new);
}
