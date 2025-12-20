package com.jujutsu.systems.ability.upgrade;

import com.jujutsu.systems.talent.AbilityTalent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

import java.util.List;

public record AbilityUpgradeBranch(Identifier id, List<AbilityTalent> upgrades) {
    public static final Codec<AbilityUpgradeBranch> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(AbilityUpgradeBranch::id),
            AbilityTalent.CODEC.listOf().fieldOf("upgrades").forGetter(AbilityUpgradeBranch::upgrades)
    ).apply(instance, AbilityUpgradeBranch::new));

    public static final PacketCodec<RegistryByteBuf, AbilityUpgradeBranch> PACKET_CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, AbilityUpgradeBranch::id,
            AbilityTalent.PACKET_CODEC.collect(PacketCodecs.toList()), AbilityUpgradeBranch::upgrades,
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

    public AbilityTalent findUpgrade(Identifier id) {
        AbilityTalent toReturn = null;
        for(AbilityTalent upgrade: upgrades()) {
            if(upgrade.id().equals(id)) {
                toReturn = upgrade;
                break;
            }
        }
        return toReturn;
    }

    public List<AbilityUpgradeReward> getAllUpgrades() {
        return upgrades.stream()
                .flatMap(upgrade -> upgrade.rewards().stream())
                .toList();
    }
}
