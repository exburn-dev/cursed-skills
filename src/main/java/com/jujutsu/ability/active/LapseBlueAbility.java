package com.jujutsu.ability.active;

import com.jujutsu.registry.ModAbilityAttributes;
import com.jujutsu.systems.ability.core.AbilityInstanceOld;
import com.jujutsu.systems.ability.core.AbilityType;
import com.jujutsu.systems.ability.data.ClientData;
import com.jujutsu.entity.LapseBlueEntity;
import com.jujutsu.systems.ability.attribute.AbilityAttributesContainer;
import com.jujutsu.systems.ability.data.IntAbilityProperty;
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
    private static final IntAbilityProperty ENTITY_ID = IntAbilityProperty.of("entityId");

    public LapseBlueAbility(int cooldownTime) {
        super(cooldownTime, true, new ClientData.Builder().addAnimation(LapseBlueAbility::renderHand).build());
    }

    @Override
    public void start(PlayerEntity player, AbilityInstanceOld instance) {
        LapseBlueEntity entity = new LapseBlueEntity(player.getWorld(), player.getUuid());
        entity.setPosition(player.getPos());
        player.getWorld().spawnEntity(entity);

        instance.set(ENTITY_ID, entity.getId());
    }

    @Override
    public void tick(PlayerEntity player, AbilityInstanceOld instance) {
        LapseBlueEntity entity = (LapseBlueEntity) player.getWorld().getEntityById(instance.get(ENTITY_ID));
        if(entity == null) return;

        Vec3d vec = player.getPos().add(player.getRotationVector(player.getPitch(), player.getYaw() - 25).multiply(0.75).add(0, 1.5, 0));

        entity.addVelocity(vec.subtract(entity.getPos()).multiply(0.15));
    }

    @Override
    public void end(PlayerEntity player, AbilityInstanceOld instance) {
        if (instance.getStatus().isCancelled() || player.getWorld().isClient()) return;

        LapseBlueEntity entity = (LapseBlueEntity) player.getWorld().getEntityById(instance.get(ENTITY_ID));
        if(entity == null) return;

        entity.setCharging(false);
        entity.setPitch(player.getPitch());
        entity.setYaw(player.getYaw());

        entity.setDamageMultiplier((float) getAbilityAttributeValue(player, ModAbilityAttributes.LAPSE_BLUE_DAMAGE_MULTIPLIER));
        entity.setStunSeconds((float) getAbilityAttributeValue(player, ModAbilityAttributes.LAPSE_BLUE_STUN));

        entity.addVelocity(entity.getRotationVector().multiply( 0.4f + (2f - 0.4f) / 100 * entity.getChargeTime() ));
    }

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstanceOld instance) {
        return instance.getUseTime() >= 70;
    }

    @Override
    public Style getStyle() {
        return Style.EMPTY.withColor(0x306ed9);
    }

    public static boolean renderHand(MatrixStack matrices, VertexConsumerProvider vertexConsumers, AbilityInstanceOld instance, ClientPlayerEntity player, PlayerEntityRenderer playerRenderer, float equipProgress, float swingProgress, int light) {
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

    @Override
    public AbilityAttributesContainer getDefaultAttributes() {
        return new AbilityAttributesContainer.Builder()
                .addBaseModifier(ModAbilityAttributes.LAPSE_BLUE_DAMAGE_MULTIPLIER, 1)
                .addBaseModifier(ModAbilityAttributes.LAPSE_BLUE_STUN, 0)
                .build();
    }
}
