package com.jujutsu.network.payload;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.ability.core.AbilityInstanceOld;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.jujutsu.systems.ability.data.AbilityPropertiesContainer;
import com.jujutsu.systems.ability.data.AbilityProperty;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Map;

public record AbilityRuntimeDataSyncS2CPayload(AbilitySlot slot, Map<AbilityProperty<?>, Comparable<?>> data) implements CustomPayload {
    public static final Identifier PACKET_ID = Jujutsu.id("ability_runtime_data_sync");
    public static final CustomPayload.Id<AbilityRuntimeDataSyncS2CPayload> ID = new CustomPayload.Id<>(PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, AbilityRuntimeDataSyncS2CPayload> CODEC = PacketCodec.tuple(
            AbilitySlot.PACKET_CODEC, AbilityRuntimeDataSyncS2CPayload::slot,
            AbilityPropertiesContainer.PACKET_CODEC_MAP, AbilityRuntimeDataSyncS2CPayload::data,
            AbilityRuntimeDataSyncS2CPayload::new
    );

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(AbilityRuntimeDataSyncS2CPayload.ID, ((payload, context) -> {
            IAbilitiesHolder holder = (IAbilitiesHolder) context.player();
            AbilityInstanceOld instance = holder.getAbilityInstance(payload.slot());

            instance.setRuntimeData(payload.data());
        }));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
