package com.jujutsu.systems.ability.upgrade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public record UpgradesData(Identifier upgradesId, int points, HashMap<Identifier, Identifier> purchasedUpgrades) {
    public static final Codec<UpgradesData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("upgradesId").forGetter(UpgradesData::upgradesId),
            Codec.INT.fieldOf("points").forGetter(UpgradesData::points),
            Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC).xmap(HashMap::new, HashMap::new)
                    .fieldOf("purchasedUpgrades").forGetter(UpgradesData::purchasedUpgrades)
    ).apply(instance, UpgradesData::new));

    public static final PacketCodec<RegistryByteBuf, UpgradesData> PACKET_CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, UpgradesData::upgradesId,
            PacketCodecs.INTEGER, UpgradesData::points,
            PacketCodecs.map(HashMap::new, Identifier.PACKET_CODEC, Identifier.PACKET_CODEC), UpgradesData::purchasedUpgrades,
            UpgradesData::new);
}
