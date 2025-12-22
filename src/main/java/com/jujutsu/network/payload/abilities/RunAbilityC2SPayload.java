package com.jujutsu.network.payload.abilities;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.ability.core.AbilityComponent;
import com.jujutsu.systems.ability.core.AbilitySlot;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RunAbilityC2SPayload(AbilitySlot slot, boolean cancel) implements CustomPayload {
    public static final Identifier PACKET_ID = Jujutsu.id("run_ability");
    public static final CustomPayload.Id<RunAbilityC2SPayload> ID = new Id<>(PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, RunAbilityC2SPayload> CODEC = PacketCodec.tuple(AbilitySlot.PACKET_CODEC, RunAbilityC2SPayload::slot, PacketCodecs.BOOL, RunAbilityC2SPayload::cancel, RunAbilityC2SPayload::new);

    public static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
            AbilityComponent component = AbilityComponent.get(context.player());
            if(component.getInstance(payload.slot) == null) return;

            if(payload.cancel()) {
                component.getInstance(payload.slot()).endAbility();
            }
            else {
                component.runAbility(payload.slot());
            }
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
