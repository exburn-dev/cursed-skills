package com.jujutsu.network.payload.talents;

import com.jujutsu.Jujutsu;
import com.jujutsu.screen.AbilityUpgradesScreen;
import com.jujutsu.api.ability.client.ClientComponentContainer;
import com.jujutsu.api.ability.upgrade.TalentsData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record TalentDataSyncS2CPayload(TalentsData data) implements CustomPacketPayload {
    public static final Identifier PACKET_ID = Jujutsu.id("talent_data_sync");
    public static final Type<TalentDataSyncS2CPayload> TYPE = new Type<>(PACKET_ID);

    public static final StreamCodec<FriendlyByteBuf, TalentDataSyncS2CPayload> CODEC = StreamCodec.composite(
            TalentsData.STREAM_CODEC, TalentDataSyncS2CPayload::data,
            TalentDataSyncS2CPayload::new
    );

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> {
            ClientComponentContainer.talentComponent.apply(payload.data());

            if(context.client().screen != null && context.client().screen instanceof AbilityUpgradesScreen upgradesScreen) {
                upgradesScreen.reload(payload.data());
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
