package com.jujutsu.network.payload.input_requests;

import com.jujutsu.Jujutsu;
import com.jujutsu.api.ability.core.AbilityComponent;
import com.jujutsu.api.ability.core.AbilityInstance;
import com.jujutsu.api.ability.core.AbilitySlot;
import com.jujutsu.api.ability.task.AbilityWaitTask;
import com.jujutsu.api.ability.task.InputWaitTask;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public record RequestedInputPressedC2SPayload(AbilitySlot slot) implements CustomPacketPayload {
    public static final Identifier ID = Jujutsu.id("requested_input_pressed");
    public static final Type<RequestedInputPressedC2SPayload> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestedInputPressedC2SPayload> CODEC = StreamCodec.composite(
            AbilitySlot.STREAM_CODEC, RequestedInputPressedC2SPayload::slot, RequestedInputPressedC2SPayload::new);

    public static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> {
            AbilityInstance instance = AbilityComponent.getInstance(context.player(), payload.slot());
            if(instance == null) return;

            AbilityWaitTask task = instance.currentlyAwaitedTask();
            if(!(task instanceof InputWaitTask inputTask)) return;

            inputTask.onInputPacketReceived(context.player());
        });
    }

    @Override
    public @NonNull Type<RequestedInputPressedC2SPayload> type() {
        return TYPE;
    }
}
