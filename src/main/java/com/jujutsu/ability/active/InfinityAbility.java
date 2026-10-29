package com.jujutsu.ability.active;

import com.google.common.collect.ImmutableList;
import com.jujutsu.Jujutsu;
import com.jujutsu.registry.custom.AbilityAttributes;
import com.jujutsu.registry.ModAttributes;
import com.jujutsu.api.ability.attribute.AbilityAttributesContainer;
import com.jujutsu.api.ability.client.ClientComponentContainer;
import com.jujutsu.api.ability.core.AbilityInstance;
import com.jujutsu.api.ability.core.AbilityInstanceData;
import com.jujutsu.api.ability.core.AbilityType;
import com.jujutsu.api.ability.data.ClientData;
import com.jujutsu.api.ability.data.IntAbilityProperty;
import com.jujutsu.api.animation.PlayerAnimations;
import com.jujutsu.api.buff.Buff;
import com.jujutsu.api.buff.conditions.TimerBuffCondition;
import com.jujutsu.api.buff.type.AttributeBuff;
import com.jujutsu.util.HandAnimationUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class InfinityAbility extends AbilityType {
    private static final IntAbilityProperty DURATION = IntAbilityProperty.of("duration");

    public InfinityAbility(int cooldownTime) {
        super(cooldownTime, false, new ClientData.Builder().addAnimation(InfinityAbility::renderHand).addOverlay((context, counter, instance) -> renderHud(context, counter, instance)).build());
    }

    @Override
    public void start(Player player, AbilityInstance instance) {
        int duration = (int) Math.floor(getAttributeValue(player, AbilityAttributes.INFINITY_DURATION)) * 20;
        instance.set(DURATION, duration);

        AttributeBuff buff = new AttributeBuff(ModAttributes.INVINCIBLE,0.5, AttributeModifier.Operation.ADD_VALUE);

        Buff.createBuff(player, buff, ImmutableList.of(new TimerBuffCondition(duration)),
                false, Jujutsu.id("infinity"));

        if(player.getWorld().isClient()) return;
        PlayerAnimations.playAnimation((ServerPlayerEntity) player, Jujutsu.id("infinity"), 1000, 50);
        instance.sendToClient();
    }

    @Override
    public void tick(Player player, AbilityInstance instance) {
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
    public AbilityAttributesContainer getDefaultAttributes() {
        return AbilityAttributesContainer.builder()
                .addBase(AbilityAttributes.INFINITY_DURATION, 8)
                .build();
    }

    @Override
    public void end(Player player, AbilityInstance instance) {}

    @Override
    public boolean isFinished(Player player, AbilityInstance instance) {
        int abilityDuration = instance.get(DURATION);
        return instance.useTime() >= abilityDuration;
    }

    @Override
    public Style getStyle() {
        return Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE);
    }

    public static boolean renderHand(MatrixStack matrices, VertexConsumerProvider vertexConsumers, AbilityInstanceData instance, ClientPlayerEntity player, PlayerEntityRenderer playerEntityRenderer, float equipProgress, float swingProgress, int light) {
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

    public static void renderHud(DrawContext context, RenderTickCounter counter, AbilityInstanceData instance) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        int abilityDuration = ClientComponentContainer.abilityComponent.getRuntimeData(instance.slot()).getOrDefault(DURATION, 0);
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
