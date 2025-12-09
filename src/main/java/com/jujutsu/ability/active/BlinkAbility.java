package com.jujutsu.ability.active;

import com.jujutsu.entity.BlinkMarkerEntity;
import com.jujutsu.systems.ability.data.AbilityData;
import com.jujutsu.systems.ability.core.AbilityInstance;
import com.jujutsu.systems.ability.core.AbilityType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.RaycastContext;

public class BlinkAbility extends AbilityType {
    public BlinkAbility(int cooldownTime) {
        super(cooldownTime, true);
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        BlinkMarkerEntity entity = new BlinkMarkerEntity(player.getWorld(), player.getUuid());
        entity.setPosition(player.getPos());

        player.getWorld().spawnEntity(entity);

        BlinkAbilityData data = new BlinkAbilityData(entity.getId(), entity.getX(), entity.getY(), entity.getZ());
        //instance.setAbilityData(data);
    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {
        //BlinkAbilityData data = instance.get(BlinkAbilityData.class, () -> (BlinkAbilityData) getInitialData());
        Vec3d pos = player.getEyePos();
        Vec3d vec = player.getRotationVector();
        Vec3d blinkPos = pos.add(vec.multiply(instance.getUseTime() * 0.5));

        BlockPos blinkBlockPos = BlockPos.ofFloored(blinkPos);
        BlockState state = player.getWorld().getBlockState(blinkBlockPos);
        if(state.isAir() && player.getWorld().getBlockState(blinkBlockPos.down()).isAir()) {
            HitResult result = player.getWorld().raycast(new RaycastContext(blinkPos, blinkPos.add(0, -100, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, ShapeContext.absent()));
            HitResult result1 = player.getWorld().raycast(new BlockStateRaycastContext(blinkPos, blinkPos.add(0, -100, 0), state1 -> !state1.isAir()));
            //blinkPos = new Vec3d(blinkPos.x, result.getPos().y, blinkPos.z);
            blinkBlockPos = BlockPos.ofFloored(blinkPos);
        }
//        else if(!state.isAir()) {
//            BlockPos currentPos = blinkBlockPos;
//            for(int i = 0; i < 10; i++) {
//                currentPos = currentPos.up();
//                if(player.getWorld().getBlockState(currentPos).isAir()) {
//                    blinkPos = currentPos.toCenterPos();
//                    blinkBlockPos = currentPos;
//                    break;
//                }
//            }
//        }

//        Vector3f color1 = new Vector3f(0.25f, 0.25f, 1);
//        ParticleEffect particle1 = new ColoredSparkParticleEffect(13, 0.95f,
//                new ColoredSparkParticleEffect.ColorTransition(color1, new Vector3f(0.5f, 0, 1)), 0, 0.1f, 50);
//
//        ParticleUtils.createCyl(particle1, blinkPos, player.getWorld(), 20, 0.5f, 0.1f);
        //Entity entity = player.getWorld().getEntityById(data.markerId());
//        if(entity instanceof BlinkMarkerEntity marker) {
//            double markerSpeed = 0.15;
//            Vec3d markerVelocity = blinkPos.subtract(marker.getPos()).multiply(markerSpeed);
//            //marker.setPosition(marker.getX(), blinkPos.y, marker.getZ());
//            marker.setVelocity(markerVelocity);
//        }
        //instance.setAbilityData(new BlinkAbilityData(data.markerId(), blinkPos.getX(), blinkPos.getY(), blinkPos.getZ()));
    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {
        //BlinkAbilityData data = instance.get(BlinkAbilityData.class, () -> (BlinkAbilityData) getInitialData());
        //player.setPos(data.x, data.y, data.z);
//
//        Entity entity = player.getWorld().getEntityById(data.markerId());
//        if(entity instanceof BlinkMarkerEntity marker) {
//            marker.remove(Entity.RemovalReason.KILLED);
//        }
    }

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstance instance) {
        return instance.getUseTime() >= 200;
    }

    public record BlinkAbilityData(int markerId, double x, double y, double z) implements AbilityData { }
}
