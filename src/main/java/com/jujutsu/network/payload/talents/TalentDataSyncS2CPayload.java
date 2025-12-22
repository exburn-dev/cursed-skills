package com.jujutsu.network.payload.talents;

import com.jujutsu.Jujutsu;
import com.jujutsu.screen.AbilityUpgradesScreen;
import com.jujutsu.systems.ability.client.ClientComponentContainer;
import com.jujutsu.systems.ability.upgrade.TalentsData;
import com.jujutsu.systems.talent.client.TalentClientComponent;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record TalentDataSyncS2CPayload(TalentsData data) implements CustomPayload {
    public static final Identifier PACKET_ID = Jujutsu.id("talent_data_sync");
    public static final Id<TalentDataSyncS2CPayload> ID = new Id<>(PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, TalentDataSyncS2CPayload> CODEC = PacketCodec.tuple(
            TalentsData.PACKET_CODEC, TalentDataSyncS2CPayload::data,
            TalentDataSyncS2CPayload::new
    );

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
            ClientComponentContainer.talentComponent.apply(payload.data());

            if(context.client().currentScreen != null && context.client().currentScreen instanceof AbilityUpgradesScreen upgradesScreen) {
                upgradesScreen.reload(payload.data());
            }
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
