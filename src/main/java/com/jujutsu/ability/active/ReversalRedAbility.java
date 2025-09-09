package com.jujutsu.ability.active;

import com.jujutsu.registry.ModAbilityAttributes;
import com.jujutsu.systems.ability.*;
import com.jujutsu.entity.ReversalRedEntity;
import com.jujutsu.systems.ability.attribute.AbilityAttributesContainer;
import com.jujutsu.util.HandAnimationUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class ReversalRedAbility extends AbilityType {
    public static final Codec<ReversalRedAbilityData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("entityId").forGetter(ReversalRedAbilityData::entityId),
            Codec.INT.fieldOf("chargeTime").forGetter(ReversalRedAbilityData::chargeTime)
    ).apply(instance, ReversalRedAbilityData::new));

    public ReversalRedAbility(int cooldownTime) {
        super(cooldownTime, true, new ClientData(ReversalRedAbility::renderHand, null));
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        if(player.getWorld().isClient()) return;
        ReversalRedEntity entity = new ReversalRedEntity(player.getWorld(), player.getUuid());
        entity.setPosition(player.getPos());
        player.getWorld().spawnEntity(entity);

        int chargeTime = (int) Math.floor(instance.getAbilityAttributeValue(player, ModAbilityAttributes.REVERSAL_RED_CHARGE_TIME)) * 20;
        instance.setAbilityData(new ReversalRedAbilityData(entity.getId(), chargeTime));
    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {
        ReversalRedAbilityData data = getData(instance);
        ReversalRedEntity entity = (ReversalRedEntity) player.getWorld().getEntityById(data.entityId());
        if(entity == null) return;

        Vec3d vec = player.getPos().add(player.getRotationVector(player.getPitch(), player.getYaw() - 25).multiply(0.75 + 0.002 * instance.getUseTime()).add(0, 1.5, 0));

        entity.addVelocity(vec.subtract(entity.getPos()).multiply(0.15));

        if(instance.getAdditionalInput() == null) {
            entity.increaseChargeTime();
        }

        if(instance.getUseTime() == data.chargeTime() - 2) {
            instance.setAdditionalInput(player, new AbilityAdditionalInput(-1, -1, 0));
            if(!player.getWorld().isClient()) {
                player.playSoundToPlayer(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1, 1);
            }
        }
    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {
        ReversalRedEntity entity = (ReversalRedEntity) player.getWorld().getEntityById(getData(instance).entityId());
        if(player.getWorld().isClient() || entity == null) return;

        entity.setCharging(false);
        entity.setPitch(player.getPitch());
        entity.setYaw(player.getYaw());

        entity.setExplosionPower((float) instance.getAbilityAttributeValue(player, ModAbilityAttributes.REVERSAL_RED_EXPLOSION_POWER));
        entity.setDamageMultiplier((float) instance.getAbilityAttributeValue(player, ModAbilityAttributes.REVERSAL_RED_DAMAGE_MULTIPLIER));
        entity.setStunSeconds((float) instance.getAbilityAttributeValue(player, ModAbilityAttributes.REVERSAL_RED_STUN));

        entity.addVelocity(entity.getRotationVector().multiply( 0.8f + 0.016 * entity.getChargeTime() ));
    }

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstance instance) {
        int chargeTime = getData(instance).chargeTime();
        return instance.getUseTime() >= chargeTime;
    }

    @Override
    public Style getStyle() {
        return Style.EMPTY.withColor(Formatting.RED);
    }

    @Override
    public AbilityAttributesContainer getDefaultAttributes() {
        return new AbilityAttributesContainer.Builder()
                .addBaseModifier(ModAbilityAttributes.REVERSAL_RED_EXPLOSION_POWER, 1)
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

    private ReversalRedAbilityData getData(AbilityInstance instance) {
        return instance.getAbilityData(ReversalRedAbilityData.class, () -> (ReversalRedAbilityData) getInitialData());
    }

    @Override
    public AbilityData getInitialData() {
        return new ReversalRedAbilityData(0, 0);
    }

    @Override
    public Codec<? extends AbilityData> getCodec() {
        return CODEC;
    }

    public record ReversalRedAbilityData(int entityId, int chargeTime) implements AbilityData {}
}
