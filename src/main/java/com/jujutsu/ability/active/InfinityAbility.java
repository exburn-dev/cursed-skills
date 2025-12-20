package com.jujutsu.ability.active;

import com.google.common.collect.ImmutableList;
import com.jujutsu.Jujutsu;
import com.jujutsu.registry.ModAbilityAttributes;
import com.jujutsu.registry.ModAttributes;
import com.jujutsu.systems.ability.attribute.AbilityAttributesContainer;
import com.jujutsu.systems.ability.attribute.SimpleAbilityAttributeContainer;
import com.jujutsu.systems.ability.core.AbilityInstance;
import com.jujutsu.systems.ability.core.AbilityType;
import com.jujutsu.systems.ability.data.ClientData;
import com.jujutsu.systems.ability.data.IntAbilityProperty;
import com.jujutsu.systems.animation.PlayerAnimations;
import com.jujutsu.systems.buff.Buff;
import com.jujutsu.systems.buff.conditions.TimerBuffPredicate;
import com.jujutsu.systems.buff.type.AttributeBuff;
import com.jujutsu.util.HandAnimationUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;

import java.util.List;

public class InfinityAbility extends AbilityType {
    private static final IntAbilityProperty DURATION = IntAbilityProperty.of("duration");

    public InfinityAbility(int cooldownTime) {
        super(cooldownTime, false, new ClientData.Builder().addAnimation(InfinityAbility::renderHand).addOverlay(InfinityAbility::renderHud).build());
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        int duration = (int) Math.floor(getAbilityAttributeValue(player, ModAbilityAttributes.INFINITY_DURATION)) * 20;
        instance.set(DURATION, duration);

        AttributeBuff buff = new AttributeBuff(ModAttributes.INVINCIBLE,0.5, EntityAttributeModifier.Operation.ADD_VALUE);

        Buff.createBuff(player, buff, ImmutableList.of(new TimerBuffPredicate(duration)),
                Buff.CancellingPolicy.ONE_OR_MORE, Jujutsu.id("infinity"));

        if(player.getWorld().isClient()) return;
        PlayerAnimations.playAnimation((ServerPlayerEntity) player, Jujutsu.id("infinity"), 1000, 50);
    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {
        if(player.getWorld().isClient()) return;
        Vec3d pos = player.getPos();
        List<Entity> entities = player.getWorld().getEntitiesByType(TypeFilter.instanceOf(Entity.class), new Box(pos.add(-2, -2, -2), pos.add(2, player.getHeight() + 2, 2)),
                (entity -> entity != player));
        for(int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            double distance = Math.max(entity.distanceTo(player), 0.001);
            if(distance > 2) continue;

            Vec3d rayPos = entity.getPos().add(0, entity.getHeight() / 2, 0);
            Vec3d vec = entity.getPos().subtract(player.getPos()).normalize();
            float width = entity.getWidth() / 2;
            HitResult result = player.getWorld().raycast(new RaycastContext(rayPos,
                    rayPos.add(vec.multiply(0.5 + width)),
                    RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
            if(!(entity instanceof LivingEntity) || result.getType() == HitResult.Type.MISS) {
                entity.addVelocity(vec.multiply(0.2 / distance));
            }
            else if(result.getType() == HitResult.Type.BLOCK) {
                Block block = player.getWorld().getBlockState(BlockPos.ofFloored(result.getPos())).getBlock();

                float damage = Math.min((float) (5f / distance) * block.getHardness(), 100f);
                Jujutsu.LOGGER.info("Infinity damage: {}", damage);
                entity.damage(player.getDamageSources().magic(), damage);
            }
        }
    }

    @Override
    public SimpleAbilityAttributeContainer getDefaultAttributes() {
        return SimpleAbilityAttributeContainer.builder()
                .addBaseModifier(ModAbilityAttributes.INFINITY_DURATION, 8)
                .build();
    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {}

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstance instance) {
        int abilityDuration = instance.get(DURATION);
        return instance.useTime() >= abilityDuration;
    }

    @Override
    public Style getStyle() {
        return Style.EMPTY.withColor(Formatting.LIGHT_PURPLE);
    }

    public static boolean renderHand(MatrixStack matrices, VertexConsumerProvider vertexConsumers, AbilityInstance instance, ClientPlayerEntity player, PlayerEntityRenderer playerEntityRenderer, float equipProgress, float swingProgress, int light) {
        if(instance.useTime() > 40) return false;

        double currentTime = Util.getMeasuringTimeMs() / 1000.0;

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

    public static void renderHud(DrawContext context, RenderTickCounter counter, AbilityInstance instance) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        int abilityDuration = instance.get(DURATION);
        float alpha;
        if(instance.useTime() >= abilityDuration - 20) {
            alpha = MathHelper.clampedLerp(0.35f, 0, (float) (instance.useTime() - abilityDuration + 20 ) / 20);
        }
        else {
            alpha = MathHelper.clampedLerp(0, 0.35f, (float) instance.useTime() / 40);
        }

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        context.setShaderColor(1.0F, 0F, 1.0F, alpha);
        context.drawTexture(Identifier.ofVanilla("textures/misc/vignette.png"), 0, 0, -90, 0.0F, 0.0F, context.getScaledWindowWidth(), context.getScaledWindowHeight(), context.getScaledWindowWidth(), context.getScaledWindowHeight());
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();

        context.setShaderColor(1.0F, 1F, 1.0F, 1f);
    }
}
