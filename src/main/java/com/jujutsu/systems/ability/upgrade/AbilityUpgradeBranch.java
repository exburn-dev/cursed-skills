package com.jujutsu.systems.ability.upgrade;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.List;

public record AbilityUpgradeBranch(List<AbilityUpgrade> upgrades) {
    public static final Codec<AbilityUpgradeBranch> CODEC = AbilityUpgrade.CODEC.listOf().xmap(AbilityUpgradeBranch::new, AbilityUpgradeBranch::upgrades);
    public static final PacketCodec<RegistryByteBuf, AbilityUpgradeBranch> PACKET_CODEC = AbilityUpgrade.PACKET_CODEC.collect(PacketCodecs.toList()).xmap(AbilityUpgradeBranch::new, AbilityUpgradeBranch::upgrades);
}
