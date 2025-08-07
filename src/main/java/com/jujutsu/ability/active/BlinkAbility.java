package com.jujutsu.ability.active;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.particle.ColoredSparkParticleEffect;
import com.jujutsu.entity.BlinkMarkerEntity;
import com.jujutsu.network.payload.SyncPlayerAbilitiesPayload;
import com.jujutsu.systems.ability.AbilityInstance;
import com.jujutsu.systems.ability.AbilityType;
import com.jujutsu.systems.ability.IPlayerJujutsuAbilitiesHolder;
import com.jujutsu.util.ParticleUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.RaycastContext;
import org.joml.Vector3f;

import java.util.List;

public class BlinkAbility extends AbilityType {
    public BlinkAbility(int cooldownTime) {
        super(cooldownTime, true);
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        //BlinkMarkerEntity entity = new BlinkMarkerEntity(player.getWorld(), player.getUuid());
        BlinkMarkerEntity entity = new BlinkMarkerEntity(player.getWorld(), player.getUuid());
        //entity.setFuse(500);
        //entity.setPickupDelay(500);
//        entity.setAiDisabled(true);
        entity.setPosition(player.getPos());

        player.getWorld().spawnEntity(entity);
        instance.getNbt().putInt("markerId", entity.getId());
    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {
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

        instance.getNbt().putDouble("posX", blinkPos.x);
        instance.getNbt().putDouble("posY", blinkPos.y);
        instance.getNbt().putDouble("posZ", blinkPos.z);

//        Vector3f color1 = new Vector3f(0.25f, 0.25f, 1);
//        ParticleEffect particle1 = new ColoredSparkParticleEffect(13, 0.95f,
//                new ColoredSparkParticleEffect.ColorTransition(color1, new Vector3f(0.5f, 0, 1)), 0, 0.1f, 50);
//
//        ParticleUtils.createCyl(particle1, blinkPos, player.getWorld(), 20, 0.5f, 0.1f);
        Entity entity = player.getWorld().getEntityById(instance.getNbt().getInt("markerId"));
        if(entity instanceof BlinkMarkerEntity marker) {
            double markerSpeed = 0.15;
            Vec3d markerVelocity = blinkPos.subtract(marker.getPos()).multiply(markerSpeed);
            //marker.setPosition(marker.getX(), blinkPos.y, marker.getZ());
            marker.setVelocity(markerVelocity);
        }
    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {
        double x = instance.getNbt().getDouble("posX");
        double y = instance.getNbt().getDouble("posY");
        double z = instance.getNbt().getDouble("posZ");
        player.setPos(x, y, z);

        Entity entity = player.getWorld().getEntityById(instance.getNbt().getInt("markerId"));
        if(entity instanceof BlinkMarkerEntity marker) {
            marker.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstance instance) {
        return instance.getUseTime() >= 200;
    }
}
