package com.jujutsu.network.payload;

import com.jujutsu.network.ModNetworkConstants;
import com.jujutsu.systems.ability.upgrade.AbilityUpgradeBranch;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;

public record SyncAbilityUpgradesPayload(HashMap<Identifier, List<AbilityUpgradeBranch>> upgrades) implements CustomPayload {
    public static final Id<SyncAbilityUpgradesPayload> ID = new Id<>(ModNetworkConstants.SYNC_ABILITY_UPGRADES_ID);
    public static final PacketCodec<RegistryByteBuf, SyncAbilityUpgradesPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.map(HashMap::new, Identifier.PACKET_CODEC, AbilityUpgradeBranch.PACKET_CODEC.collect(PacketCodecs.toList())),
            SyncAbilityUpgradesPayload::upgrades,
            SyncAbilityUpgradesPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
