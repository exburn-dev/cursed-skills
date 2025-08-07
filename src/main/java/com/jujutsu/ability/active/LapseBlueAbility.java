package com.jujutsu.ability.active;

import com.jujutsu.entity.ReversalRedEntity;
import com.jujutsu.systems.ability.AbilityInstance;
import com.jujutsu.systems.ability.AbilityType;
import com.jujutsu.systems.ability.ClientData;
import com.jujutsu.entity.LapseBlueEntity;
import com.jujutsu.util.HandAnimationUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class LapseBlueAbility extends AbilityType {
    public LapseBlueAbility(int cooldownTime) {
        super(cooldownTime, true, new ClientData.Builder().addAnimation(LapseBlueAbility::renderHand).build());
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        LapseBlueEntity entity = new LapseBlueEntity(player.getWorld(), player.getUuid());
        entity.setPosition(player.getPos());
        player.getWorld().spawnEntity(entity);

        instance.getNbt().putInt("entityId", entity.getId());
    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {
        LapseBlueEntity entity = (LapseBlueEntity) player.getWorld().getEntityById(instance.getNbt().getInt("entityId"));
        if(entity == null) return;

        Vec3d vec = player.getPos().add(player.getRotationVector(player.getPitch(), player.getYaw() - 25).multiply(0.75).add(0, 1.5, 0));

        entity.addVelocity(vec.subtract(entity.getPos()).multiply(0.15));
    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {
        if (instance.isCancelled() || player.getWorld().isClient()) return;

        LapseBlueEntity entity = (LapseBlueEntity) player.getWorld().getEntityById(instance.getNbt().getInt("entityId"));
        if(entity == null) return;

        entity.setCharging(false);
        entity.setPitch(player.getPitch());
        entity.setYaw(player.getYaw());
        entity.addVelocity(entity.getRotationVector().multiply( 0.4f + (2f - 0.4f) / 100 * entity.getChargeTime() ));

        instance.getNbt().remove("entityId");
    }

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstance instance) {
        return instance.getUseTime() >= 70;
    }

    @Override
    public Style getStyle() {
        return Style.EMPTY.withColor(0x306ed9);
    }

    public static boolean renderHand(MatrixStack matrices, VertexConsumerProvider vertexConsumers, AbilityInstance instance, ClientPlayerEntity player, PlayerEntityRenderer playerRenderer, float equipProgress, float swingProgress, int light) {
        matrices.push();

        double currentTime = Util.getMeasuringTimeMs() / 1000.0;
        float lerpedAmount = MathHelper.sin((float) currentTime * 6);

        HandAnimationUtils.applyDefaultHandTransform(matrices, false);
        matrices.translate(0.2971014492753621f, 0.13043478260869557f, 0.10144927536231862f);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-49.56521739130434f + lerpedAmount * 5));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0.652173913043498f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(13.695652173913047f));

        playerRenderer.renderLeftArm(matrices, vertexConsumers, light, player);

        matrices.pop();
        return true;
    }
}
