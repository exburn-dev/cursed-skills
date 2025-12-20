package com.jujutsu.ability.active;

import com.jujutsu.registry.ModAbilityAttributes;
import com.jujutsu.entity.ReversalRedEntity;
import com.jujutsu.systems.ability.attribute.AbilityAttributesContainer;
import com.jujutsu.systems.ability.attribute.SimpleAbilityAttributeContainer;
import com.jujutsu.systems.ability.core.AbilityInstance;
import com.jujutsu.systems.ability.core.AbilityType;
import com.jujutsu.systems.ability.data.InputRequest;
import com.jujutsu.systems.ability.data.ClientData;
import com.jujutsu.systems.ability.data.IntAbilityProperty;
import com.jujutsu.util.HandAnimationUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class ReversalRedAbility extends AbilityType {
    private static final IntAbilityProperty ENTITY_ID = IntAbilityProperty.of("entityId");
    private static final IntAbilityProperty CHARGE_TIME = IntAbilityProperty.of("chargeTime");

    public ReversalRedAbility(int cooldownTime) {
        super(cooldownTime, true, new ClientData(ReversalRedAbility::renderHand, null));
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        if(player.getWorld().isClient()) return;
        ReversalRedEntity entity = new ReversalRedEntity(player.getWorld(), player.getUuid());
        entity.setPosition(player.getPos());
        player.getWorld().spawnEntity(entity);

        int chargeTime = (int) Math.floor(getAbilityAttributeValue(player, ModAbilityAttributes.REVERSAL_RED_CHARGE_TIME)) * 20;
        setData(instance, entity.getId(), chargeTime);
    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {
        ReversalRedEntity entity = (ReversalRedEntity) player.getWorld().getEntityById(instance.get(ENTITY_ID));
        if(entity == null) return;

        Vec3d vec = player.getPos().add(player.getRotationVector(player.getPitch(), player.getYaw() - 25).multiply(0.75 + 0.002 * instance.useTime()).add(0, 1.5, 0));

        entity.addVelocity(vec.subtract(entity.getPos()).multiply(0.15));

        if(!instance.status().isWaiting()) {
            entity.increaseChargeTime();
        }

        if(instance.useTime() == instance.get(CHARGE_TIME) - 2) {
            instance.requestInput(InputRequest.mouseRequest(0, (player1) -> ActionResult.SUCCESS).build());
            if(!player.getWorld().isClient()) {
                player.playSoundToPlayer(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1, 1);
            }
        }
    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {
        ReversalRedEntity entity = (ReversalRedEntity) player.getWorld().getEntityById(instance.get(ENTITY_ID));
        if(player.getWorld().isClient() || entity == null) return;

        entity.setCharging(false);
        entity.setPitch(player.getPitch());
        entity.setYaw(player.getYaw());

        entity.setExplosionPower((float) getAbilityAttributeValue(player, ModAbilityAttributes.REVERSAL_RED_EXPLOSION_POWER));
        entity.setDamageMultiplier((float) getAbilityAttributeValue(player, ModAbilityAttributes.REVERSAL_RED_DAMAGE_MULTIPLIER));
        entity.setStunSeconds((float) getAbilityAttributeValue(player, ModAbilityAttributes.REVERSAL_RED_STUN));

        entity.addVelocity(entity.getRotationVector().multiply( 0.8f + 0.016 * entity.getChargeTime() ));
    }

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstance instance) {
        int chargeTime = instance.get(CHARGE_TIME);
        return instance.useTime() >= chargeTime;
    }

    @Override
    public Style getStyle() {
        return Style.EMPTY.withColor(Formatting.RED);
    }

    @Override
    public SimpleAbilityAttributeContainer getDefaultAttributes() {
        return SimpleAbilityAttributeContainer.builder()
                .addBaseModifier(ModAbilityAttributes.REVERSAL_RED_EXPLOSION_POWER, 2)
                .addBaseModifier(ModAbilityAttributes.REVERSAL_RED_DAMAGE_MULTIPLIER, 1)
                .addBaseModifier(ModAbilityAttributes.REVERSAL_RED_STUN, 0)
                .addBaseModifier(ModAbilityAttributes.REVERSAL_RED_CHARGE_TIME, 3)
                .build();
    }

    public static boolean renderHand(MatrixStack matrices, VertexConsumerProvider vertexConsumers, AbilityInstance instance, ClientPlayerEntity player, PlayerEntityRenderer playerEntityRenderer, float equipProgress, float swingProgress, int light) {
        matrices.push();

        HandAnimationUtils.applyDefaultHandTransform(matrices, false);
        matrices.translate(0.19565217391304346f, 0.09420289855072483f, -0.04347826086956519f);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-58.69565217391305f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-30.00000000000002f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-0.652173913043498f));

        playerEntityRenderer.renderLeftArm(matrices, vertexConsumers, light, player);

        matrices.pop();

        return true;
    }

    private void setData(AbilityInstance instance, int entityId, int chargeTime) {
        instance.set(ENTITY_ID, entityId);
        instance.set(CHARGE_TIME, chargeTime);
    }
}
