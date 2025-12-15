package com.jujutsu.network.payload;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.ability.core.AbilityComponent;
import com.jujutsu.systems.ability.holder.PlayerJujutsuAbilities;
import com.jujutsu.network.ModNetworkConstants;
import com.jujutsu.systems.ability.upgrade.UpgradesData;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record AbilityComponentSyncS2CPayload(AbilityComponent component) implements CustomPayload {
    public static final Identifier PACKET_ID = Jujutsu.id("ability_component_sync");
    public static final CustomPayload.Id<AbilityComponentSyncS2CPayload> ID = new Id<>(ModNetworkConstants.SYNC_PLAYER_ABILITIES_ID);

    public static final PacketCodec<RegistryByteBuf, AbilityComponentSyncS2CPayload> CODEC = PacketCodec.tuple(
            PlayerJujutsuAbilities.PACKET_CODEC, AbilityComponentSyncS2CPayload::abilities,
            UpgradesData.PACKET_CODEC, AbilityComponentSyncS2CPayload::upgradesData,
            AbilityComponentSyncS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
