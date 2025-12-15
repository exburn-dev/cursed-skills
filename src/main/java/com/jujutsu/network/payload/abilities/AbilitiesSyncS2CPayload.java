package com.jujutsu.network.payload.abilities;

import com.jujutsu.Jujutsu;
import com.jujutsu.screen.AbilityUpgradesScreen;
import com.jujutsu.systems.ability.client.ClientComponentContainer;
import com.jujutsu.systems.ability.core.AbilityInstanceData;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
import com.jujutsu.systems.ability.holder.IPlayerJujutsuAbilitiesHolder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;

public record AbilitiesSyncS2CPayload(List<AbilityInstanceData> instanceDataList) implements CustomPayload {
    public static final Identifier PACKET_ID = Jujutsu.id("ability_component_sync");
    public static final CustomPayload.Id<AbilitiesSyncS2CPayload> ID = new Id<>(PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, AbilitiesSyncS2CPayload> CODEC = PacketCodec.tuple(
            AbilityInstanceData.PACKET_CODEC.collect(PacketCodecs.toList()), AbilitiesSyncS2CPayload::instanceDataList,
            AbilitiesSyncS2CPayload::new);

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(AbilitiesSyncS2CPayload.ID, (payload, context) -> {
            ClientComponentContainer.abilityComponent.apply(payload.instanceDataList());

            //TODO: reload upgrades screen
//            if(context.client().currentScreen != null && context.client().currentScreen instanceof AbilityUpgradesScreen upgradesScreen) {
//                upgradesScreen.reload(payload.upgradesData());
//            }
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
