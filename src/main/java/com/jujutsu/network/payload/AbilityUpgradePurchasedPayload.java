package com.jujutsu.network.payload;

import com.jujutsu.network.ModNetworkConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record AbilityUpgradePurchasedPayload(Identifier branchId, Identifier upgradeId) implements CustomPayload {
    public static final Id<AbilityUpgradePurchasedPayload> ID = new Id<>(ModNetworkConstants.ABILITY_UPGRADE_PURCHASED_ID);
    public static final PacketCodec<RegistryByteBuf, AbilityUpgradePurchasedPayload> CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, AbilityUpgradePurchasedPayload::branchId,
            Identifier.PACKET_CODEC, AbilityUpgradePurchasedPayload::upgradeId,
            AbilityUpgradePurchasedPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
