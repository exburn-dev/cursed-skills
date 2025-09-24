package com.jujutsu.util;

import com.jujutsu.Jujutsu;
import com.jujutsu.event.server.DelayedTasks;
import com.jujutsu.mixin.FallingBlockAccessor;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class BlockExplosions {
    public static final Map<Block, List<Block>> EXPLOSION_CONSEQUENCES = new HashMap<>();

    private static void createBlockExplosion(World world, Iterable<BlockPos> blocks, BlockPos center, double radius, Predicate<BlockState> predicate) {
        for (BlockPos pos : blocks) {
            if(!pos.isWithinDistance(center, radius)) continue;

            BlockState state = world.getBlockState(pos);
            if(!state.isAir() && predicate.test(state)) {
                Vec3d posVec = pos.toCenterPos();
                Vec3d velocity = posVec.subtract(center.toCenterPos()).multiply(0.1).add(0, 1, 0);

                FallingBlockEntity fb = new FallingBlockEntity(EntityType.FALLING_BLOCK, world);
                fb.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                ((FallingBlockAccessor) fb).setBlock(state);
                world.setBlockState(pos, state.getFluidState().getBlockState(), Block.NOTIFY_ALL);
                world.spawnEntity(fb);

                ServerWorld serverWorld = (ServerWorld) world;

                DelayedTasks.runDelayedTask(serverWorld.getServer(), 1,() -> {
                    fb.setVelocity(velocity.x, velocity.y, velocity.z);
                    EntityVelocityUpdateS2CPacket pkt = new EntityVelocityUpdateS2CPacket(fb);
                    for (ServerPlayerEntity pl : PlayerLookup.tracking(fb)) {
                        pl.networkHandler.sendPacket(pkt);
                    }
                });
            }
        }
    }

    public static void createBlockExplosion(World world, BlockPos center, double radius, Predicate<BlockState> predicate) {
        if(world.isClient()) return;
        Box box = Box.of(center.toCenterPos(), radius * 2, radius * 2, radius * 2);
        BlockPos startPos = BlockPos.ofFloored(box.minX, box.minY, box.minZ);
        BlockPos endPos = BlockPos.ofFloored(box.maxX, box.maxY, box.maxZ);

        createBlockExplosion(world, BlockPos.iterate(startPos, endPos), center, radius, predicate);
    }

    public static void applyExplosionConsequences(Vec3d pos, double radius, World world) {
        Box box = Box.of(pos, radius * 2, radius * 2, radius * 2);
        BlockPos startPos = BlockPos.ofFloored(box.minX, box.minY, box.minZ);
        BlockPos endPos = BlockPos.ofFloored(box.maxX, box.maxY, box.maxZ);

        double fadeRadius = 5;
        for(BlockPos blockPos: BlockPos.iterate(startPos, endPos)) {
            BlockState currentState = world.getBlockState(blockPos);
            if(!EXPLOSION_CONSEQUENCES.containsKey(currentState.getBlock())) continue;

            double distance = Math.sqrt(blockPos.getSquaredDistance(pos));
            if(distance > radius + fadeRadius) continue;

            double chance = 0.5;
            if(distance > radius) {
                chance -= ((distance - radius) / fadeRadius) * chance;
            }

            if(chance >= world.getRandom().nextDouble()) {
                List<Block> possibleBlocks = EXPLOSION_CONSEQUENCES.get(currentState.getBlock());
                Block block = possibleBlocks.get(world.getRandom().nextInt(possibleBlocks.size()));

                world.setBlockState(blockPos, block.getDefaultState());
            }
        }
    }

    static {
        EXPLOSION_CONSEQUENCES.put(Blocks.STONE, List.of(Blocks.COBBLESTONE, Blocks.BLACKSTONE, Blocks.GRAVEL));
        EXPLOSION_CONSEQUENCES.put(Blocks.GRASS_BLOCK, List.of(Blocks.DIRT, Blocks.PODZOL, Blocks.COARSE_DIRT));
    }
}
