package com.jujutsu.ability.active;

import com.jujutsu.entity.ReversalRedEntity;
import com.jujutsu.registry.ModAbilityAttributes;
import com.jujutsu.systems.ability.AbilityData;
import com.jujutsu.systems.ability.AbilityInstance;
import com.jujutsu.systems.ability.AbilityType;
import com.jujutsu.systems.ability.ClientData;
import com.jujutsu.entity.LapseBlueEntity;
import com.jujutsu.systems.ability.attribute.AbilityAttributesContainer;
import com.jujutsu.util.HandAnimationUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
    public static final Codec<LapseBlueAbilityData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("entityId").forGetter(LapseBlueAbilityData::entityId)
    ).apply(instance, LapseBlueAbilityData::new));

    public LapseBlueAbility(int cooldownTime) {
        super(cooldownTime, true, new ClientData.Builder().addAnimation(LapseBlueAbility::renderHand).build());
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        LapseBlueEntity entity = new LapseBlueEntity(player.getWorld(), player.getUuid());
        entity.setPosition(player.getPos());
        player.getWorld().spawnEntity(entity);

        instance.setAbilityData(new LapseBlueAbilityData(entity.getId()));
    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {
        LapseBlueAbilityData data = getData(instance);

        LapseBlueEntity entity = (LapseBlueEntity) player.getWorld().getEntityById(data.entityId());
        if(entity == null) return;

        Vec3d vec = player.getPos().add(player.getRotationVector(player.getPitch(), player.getYaw() - 25).multiply(0.75).add(0, 1.5, 0));

        entity.addVelocity(vec.subtract(entity.getPos()).multiply(0.15));
    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {
        if (instance.getStatus().isCancelled() || player.getWorld().isClient()) return;

        LapseBlueAbilityData data = getData(instance);
        LapseBlueEntity entity = (LapseBlueEntity) player.getWorld().getEntityById(data.entityId());
        if(entity == null) return;

        entity.setCharging(false);
        entity.setPitch(player.getPitch());
        entity.setYaw(player.getYaw());

        entity.setDamageMultiplier((float) instance.getAbilityAttributeValue(player, ModAbilityAttributes.LAPSE_BLUE_DAMAGE_MULTIPLIER));
        entity.setStunSeconds((float) instance.getAbilityAttributeValue(player, ModAbilityAttributes.LAPSE_BLUE_STUN));

        entity.addVelocity(entity.getRotationVector().multiply( 0.4f + (2f - 0.4f) / 100 * entity.getChargeTime() ));
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

    private LapseBlueAbilityData getData(AbilityInstance instance) {
        return instance.getAbilityData(LapseBlueAbilityData.class, () -> (LapseBlueAbilityData) getInitialData());
    }

    @Override
    public AbilityAttributesContainer getDefaultAttributes() {
        return new AbilityAttributesContainer.Builder()
                .addBaseModifier(ModAbilityAttributes.LAPSE_BLUE_DAMAGE_MULTIPLIER, 1)
                .addBaseModifier(ModAbilityAttributes.LAPSE_BLUE_STUN, 0)
                .build();
    }

    @Override
    public AbilityData getInitialData() {
        return new LapseBlueAbilityData(0);
    }

    @Override
    public Codec<? extends AbilityData> getCodec() {
        return CODEC;
    }

    public record LapseBlueAbilityData(int entityId) implements AbilityData {}
}
