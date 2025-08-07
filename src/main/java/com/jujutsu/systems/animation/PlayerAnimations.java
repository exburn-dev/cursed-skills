package com.jujutsu.systems.animation;

import com.jujutsu.Jujutsu;
import com.jujutsu.network.payload.PlayAnimationPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

public class PlayerAnimations {

    public static void playAnimation(ServerPlayerEntity player, Identifier animation, int priority, int distance) {
        Box box = Box.of(player.getPos(), distance, distance, distance);
        AnimationData data = new AnimationData(player.getUuid(), animation, priority);

        for (ServerPlayerEntity playerEntity :  ((ServerWorld) player.getWorld()).getPlayers()) {
            if (box.contains(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ())) {
                ServerPlayNetworking.send(playerEntity, new PlayAnimationPayload(data));
            }
        }
        ServerPlayNetworking.send(player, new PlayAnimationPayload(data));
    }
}
