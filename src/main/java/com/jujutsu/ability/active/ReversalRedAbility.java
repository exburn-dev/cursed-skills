package com.jujutsu.ability.active;

import com.jujutsu.systems.ability.*;
import com.jujutsu.entity.ReversalRedEntity;
import com.jujutsu.util.HandAnimationUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
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
            Codec.INT.fieldOf("entityId").forGetter(ReversalRedAbilityData::entityId)
    ).apply(instance, ReversalRedAbilityData::new));

    public ReversalRedAbility(int cooldownTime) {
        super(cooldownTime, true, new ClientData(ReversalRedAbility::renderHand, ReversalRedAbility::renderHud));
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        if(player.getWorld().isClient()) return;
        ReversalRedEntity entity = new ReversalRedEntity(player.getWorld(), player.getUuid());
        entity.setPosition(player.getPos());
        player.getWorld().spawnEntity(entity);

        instance.setAbilityData(new ReversalRedAbilityData(entity.getId()));
    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {
        ReversalRedEntity entity = (ReversalRedEntity) player.getWorld().getEntityById(getData(instance).entityId());
        if(entity == null) return;

        Vec3d vec = player.getPos().add(player.getRotationVector(player.getPitch(), player.getYaw() - 25).multiply(0.75).add(0, 1.5, 0));

        entity.addVelocity(vec.subtract(entity.getPos()).multiply(0.15));

        if(instance.getAdditionalInput() == null) {
            entity.increaseChargeTime();
        }

        if(instance.getUseTime() == 98) {
            instance.setAdditionalInput(player, new AbilityAdditionalInput(-1, -1, 0));
            if(!player.getWorld().isClient()) {
                player.playSoundToPlayer(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1, 1);
            }
        }
    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {
        ReversalRedEntity entity = (ReversalRedEntity) player.getWorld().getEntityById(getData(instance).entityId());
        if(entity == null) return;

        entity.setCharging(false);
        entity.setPitch(player.getPitch());
        entity.setYaw(player.getYaw());
        entity.addVelocity(entity.getRotationVector().multiply( 0.4f + (2f - 0.4f) / 100 * entity.getChargeTime() ));
    }

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstance instance) {
        return instance.getUseTime() >= 100;
    }

    @Override
    public Style getStyle() {
        return Style.EMPTY.withColor(Formatting.RED);
    }

    public static void renderHud(DrawContext context, RenderTickCounter counter, AbilityInstance instance) {
//        MinecraftClient client = MinecraftClient.getInstance();
//        if (client == null || client.player == null) return;
//
//        MatrixStack matrices = context.getMatrices();
//        int tick = ((int) Util.getMeasuringTimeMs() / 100 % 10) + 1;
//
//        RenderSystem.disableDepthTest();
//        RenderSystem.depthMask(false);
//        RenderSystem.enableBlend();
//
//        matrices.push();
//        matrices.translate(Math.sin(tick), 0, 0);
//        context.drawTexture(Jujutsu.getId(String.format("textures/misc/reversal_red%d.png", tick)), 240, context.getScaledWindowHeight() / 2 - 50, -90, 0.0F, 0.0F, 64, 64, 64, 64);
//
//        matrices.pop();
//
//        RenderSystem.disableBlend();
//        RenderSystem.depthMask(true);
//        RenderSystem.enableDepthTest();
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
        return new ReversalRedAbilityData(0);
    }

    @Override
    public Codec<? extends AbilityData> getCodec() {
        return CODEC;
    }

    public record ReversalRedAbilityData(int entityId) implements AbilityData {}
}
