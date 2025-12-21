package com.jujutsu.network.payload.talents;

import com.jujutsu.Jujutsu;
import com.jujutsu.event.resource.TalentBranchesResourceLoader;
import com.jujutsu.event.resource.TalentResourceLoader;
import com.jujutsu.event.resource.TalentTreeResourceLoader;
import com.jujutsu.systems.talent.AbilityTalent;
import com.jujutsu.systems.talent.TalentBranch;
import com.jujutsu.systems.talent.TalentTree;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public record TalentResourcesSyncS2CPayload(Map<Identifier, AbilityTalent> talents, Map<Identifier,
        TalentBranch> branches, Map<Identifier, TalentTree> trees) implements CustomPayload {

    public static final Identifier PACKET_ID = Jujutsu.id("talent_sync");
    public static final Id<TalentResourcesSyncS2CPayload> ID = new Id<>(PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, TalentResourcesSyncS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.map(HashMap::new, Identifier.PACKET_CODEC, AbilityTalent.PACKET_CODEC),
            TalentResourcesSyncS2CPayload::talents,

            PacketCodecs.map(HashMap::new, Identifier.PACKET_CODEC, TalentBranch.PACKET_CODEC),
            TalentResourcesSyncS2CPayload::branches,

            PacketCodecs.map(HashMap::new, Identifier.PACKET_CODEC, TalentTree.PACKET_CODEC),
            TalentResourcesSyncS2CPayload::trees,

            TalentResourcesSyncS2CPayload::new);

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
            TalentResourceLoader.getInstance().setTalents(payload.talents());
            TalentBranchesResourceLoader.getInstance().setBranches(payload.branches());
            TalentTreeResourceLoader.getInstance().setTrees(payload.trees());
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
