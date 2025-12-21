package com.jujutsu.network.payload.talents;

import com.jujutsu.Jujutsu;
import com.jujutsu.event.resource.TalentBranchesResourceLoader;
import com.jujutsu.event.resource.TalentResourceLoader;
import com.jujutsu.systems.talent.AbilityTalent;
import com.jujutsu.systems.talent.TalentTreeValidator;
import com.jujutsu.systems.talent.TalentComponent;
import com.jujutsu.systems.talent.TalentTree;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record TalentPurchasedC2SPayload(Identifier branchId, Identifier upgradeId) implements CustomPayload {
    public static final Identifier PACKET_ID = Jujutsu.id("talent_purchased");
    public static final Id<TalentPurchasedC2SPayload> ID = new Id<>(PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, TalentPurchasedC2SPayload> CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, TalentPurchasedC2SPayload::branchId,
            Identifier.PACKET_CODEC, TalentPurchasedC2SPayload::upgradeId,
            TalentPurchasedC2SPayload::new);

    public static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(TalentPurchasedC2SPayload.ID, (payload, context) -> {

            TalentComponent component = TalentComponent.get(context.player());
            TalentTreeValidator validator = new TalentTreeValidator(component.currentTree());

            if(validator.validate(payload.branchId(), payload.upgradeId(), component)) {
                AbilityTalent talent = TalentResourceLoader.getInstance().get(payload.upgradeId());
                talent.apply(context.player());

                component.talentPurchased(payload.branchId(), talent);
            }

            component.sendToClient();
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
