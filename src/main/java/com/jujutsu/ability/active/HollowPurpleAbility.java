package com.jujutsu.ability.active;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.particle.ColoredSparkParticleEffect;
import com.jujutsu.systems.ability.AbilityInstance;
import com.jujutsu.systems.ability.AbilityType;
import com.jujutsu.systems.ability.ClientData;
import com.jujutsu.entity.HollowPurpleEntity;
import com.jujutsu.systems.animation.PlayerAnimations;
import com.jujutsu.util.HandAnimationUtils;
import com.jujutsu.util.ParticleUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class HollowPurpleAbility extends AbilityType {
    public HollowPurpleAbility(int cooldownTime) {
        super(cooldownTime, true, new ClientData.Builder().addAnimation(HollowPurpleAbility::renderHand).build());
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        if(player.getWorld().isClient()) return;
        PlayerAnimations.playAnimation((ServerPlayerEntity) player, Jujutsu.getId("hollow_purple"), 1000, 50);
    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {
        if(instance.getUseTime() == 59 && !player.getWorld().isClient()) {
            HollowPurpleEntity entity = new HollowPurpleEntity(player.getWorld(), player.getUuid());
            entity.setYaw(player.getYaw());
            entity.setPitch(player.getPitch());
            entity.setPosition(player.getEyePos().add(0, 0.5, 0).add(player.getRotationVector().multiply(5)));
            player.getWorld().spawnEntity(entity);
        }
        Vec3d pos = player.getEyePos().add(0, (double) instance.getUseTime() / 120, 0).add(player.getRotationVector().multiply(5));
        renderChargeBall(player.getWorld(), pos, instance.getUseTime());
    }

    private void renderChargeBall(World world, Vec3d pos, int chargeTime) {
        if(chargeTime % 5 != 0) return;

        Supplier<ParticleEffect> effect = () -> new ColoredSparkParticleEffect(
                4, 0.8f,
                new ColoredSparkParticleEffect.ColorTransition(new Vector3f(0.5f, 0, 0.75f), new Vector3f(0.5f, 0, 0.75f)),
                0, 2f,
                10
        );
        ParticleUtils.createBall(effect, pos, world, 100, chargeTime * 0.025f, 0.01f);

        Supplier<ParticleEffect> effect1 = () -> new ColoredSparkParticleEffect(
                6, 0.95f,
                new ColoredSparkParticleEffect.ColorTransition(new Vector3f(0.5f, 0, 0.75f), new Vector3f(0.4f, 0, 0.4f)),
                0, 2f,
                20
        );
        ParticleUtils.createBall(effect1, pos, world, 3, 2, -0.05f);
    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {
        Vec3d pos = player.getEyePos().add(0, 0.5, 0).add(player.getRotationVector().multiply(5));
        if(instance.getStatus().isCancelled()) {
            player.getWorld().playSound(player, pos.x, pos.y, pos.z, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.MASTER, 1f, 0.5f);
            return;
        }

        player.getWorld().playSound(player, pos.x, pos.y, pos.z, SoundEvents.ITEM_TOTEM_USE, SoundCategory.MASTER, 1, 0.5f);
        if(player.getWorld().isClient()) return;
        player.getWorld().playSound(player, pos.x, pos.y, pos.z, SoundEvents.ITEM_TOTEM_USE, SoundCategory.MASTER, 1, 0.5f);
    }

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstance instance) {
        return instance.getUseTime() >= 60;
    }

    @Override
    public Style getStyle() {
        return Style.EMPTY.withColor(Formatting.LIGHT_PURPLE);
    }

    public static boolean renderHand(MatrixStack matrices, VertexConsumerProvider vertexConsumers, AbilityInstance instance, ClientPlayerEntity player, PlayerEntityRenderer playerEntityRenderer, float equipProgress, float swingProgress, int light) {
        matrices.push();

        double currentTime = Util.getMeasuringTimeMs() / 1000.0;
        float lerpedAmount = MathHelper.sin((float) currentTime * 6);

        HandAnimationUtils.applyDefaultHandTransform(matrices, true);
        matrices.translate(0.02898550724637694, 0.2463768115942031, -0.2971014492753621);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-33.91304347826089f + lerpedAmount * 5));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(43.69565217391306f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-3.2608695652173765f));

        playerEntityRenderer.renderRightArm(matrices, vertexConsumers, light, player);

        matrices.pop();
        matrices.push();

        HandAnimationUtils.applyDefaultHandTransform(matrices, false);
        matrices.translate(0.0f, 0.18260869565217384f, -0.10869565217391308f);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-30.00000000000002f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-13.695652173913047f));

        playerEntityRenderer.renderLeftArm(matrices, vertexConsumers, light, player);

        matrices.pop();

        return true;
    }
}
