package com.jujutsu.util;

import com.jujutsu.event.server.DelayedTasks;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class BlockExplosions {
    public static final Map<Block, List<Block>> EXPLOSION_CONSEQUENCES = new HashMap<>();

    private static void createBlockExplosion(ServerLevel level, Iterable<BlockPos> blocks, BlockPos center, double radius, Predicate<BlockState> predicate) {
        for (BlockPos pos : blocks) {
            if(!pos.closerThan(center, radius)) continue;

            BlockState state = level.getBlockState(pos);
            if(!state.isAir() && predicate.test(state)) {
                Vec3 posVec = pos.getCenter();
                Vec3 velocity = posVec.subtract(center.getCenter()).scale(0.1).add(0, 1, 0);

                FallingBlockEntity fb = FallingBlockEntity.fall(level, pos, state);

                DelayedTasks.runDelayedTask(level.getServer(), 1,() -> {
                    fb.setDeltaMovement(velocity.x, velocity.y, velocity.z);
                    ClientboundSetEntityMotionPacket pkt = new ClientboundSetEntityMotionPacket(fb);
                    for (ServerPlayer pl : PlayerLookup.tracking(fb)) {
                        pl.connection.send(pkt);
                    }
                });
            }
        }
    }

    public static void createBlockExplosion(Level level, BlockPos center, double radius, Predicate<BlockState> predicate) {
        if(level.isClientSide()) return;

        AABB box = AABB.ofSize(center.getCenter(), radius * 2, radius * 2, radius * 2);
        createBlockExplosion((ServerLevel) level, BlockPos.betweenClosed(box), center, radius, predicate);
    }

    public static void applyExplosionConsequences(Vec3 pos, double radius, Level level) {
        AABB box = AABB.ofSize(pos, radius * 2, radius * 2, radius * 2);

        double fadeRadius = 5;
        for(BlockPos blockPos: BlockPos.betweenClosed(box)) {
            BlockState currentState = level.getBlockState(blockPos);
            if(!EXPLOSION_CONSEQUENCES.containsKey(currentState.getBlock())) continue;

            double distance = Math.sqrt(blockPos.distToCenterSqr(pos));
            if(distance > radius + fadeRadius) continue;

            double chance = 0.5;
            if(distance > radius) {
                chance -= ((distance - radius) / fadeRadius) * chance;
            }

            if(chance >= level.getRandom().nextDouble()) {
                List<Block> possibleBlocks = EXPLOSION_CONSEQUENCES.get(currentState.getBlock());
                Block block = possibleBlocks.get(level.getRandom().nextInt(possibleBlocks.size()));

                level.setBlock(blockPos, block.defaultBlockState(), 5);
            }
        }
    }

    static {
        EXPLOSION_CONSEQUENCES.put(Blocks.STONE, List.of(Blocks.COBBLESTONE, Blocks.BLACKSTONE, Blocks.GRAVEL));
        EXPLOSION_CONSEQUENCES.put(Blocks.GRASS_BLOCK, List.of(Blocks.DIRT, Blocks.PODZOL, Blocks.COARSE_DIRT));
    }
}
