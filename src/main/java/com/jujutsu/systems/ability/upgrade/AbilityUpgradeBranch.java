package com.jujutsu.systems.ability.upgrade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

import java.util.List;

public record AbilityUpgradeBranch(Identifier id, List<AbilityUpgrade> upgrades) {
    public static final Codec<AbilityUpgradeBranch> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(AbilityUpgradeBranch::id),
            AbilityUpgrade.CODEC.listOf().fieldOf("upgrades").forGetter(AbilityUpgradeBranch::upgrades)
    ).apply(instance, AbilityUpgradeBranch::new));

    public static final PacketCodec<RegistryByteBuf, AbilityUpgradeBranch> PACKET_CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, AbilityUpgradeBranch::id,
            AbilityUpgrade.PACKET_CODEC.collect(PacketCodecs.toList()), AbilityUpgradeBranch::upgrades,
            AbilityUpgradeBranch::new);

    public static int findPlayerLastPurchasedBranchIndex(List<AbilityUpgradeBranch> branches, UpgradesData playerData) {
        int branchIndex = -1;
        for(int i = 0; i < branches.size(); i++) {
            Identifier branchId = branches.get(i).id();
            if(playerData.purchasedUpgrades().containsKey(branchId)) {
                branchIndex = Math.max(branchIndex, i);
            }
        }
        return branchIndex;
    }
}
