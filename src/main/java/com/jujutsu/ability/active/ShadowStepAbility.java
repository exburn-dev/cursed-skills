package com.jujutsu.ability.active;

import com.google.common.collect.ImmutableList;
import com.jujutsu.Jujutsu;
import com.jujutsu.registry.ModAttributes;
import com.jujutsu.registry.ModSounds;
import com.jujutsu.systems.ability.AbilityInstance;
import com.jujutsu.systems.ability.AbilityType;
import com.jujutsu.systems.ability.ClientData;
import com.jujutsu.systems.buff.Buff;
import com.jujutsu.systems.buff.conditions.TimeCancellingCondition;
import com.jujutsu.util.VisualEffectUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ShadowStepAbility extends AbilityType {
    public ShadowStepAbility(int cooldownTime) {
        super(cooldownTime, false, new ClientData(null, ShadowStepAbility::renderHud));
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        if(player.getWorld().isClient()) return;

        Buff.createBuff(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, ImmutableList.of(new TimeCancellingCondition(30)), Buff.CancellingPolicy.ONE_OR_MORE,
                -100, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, Jujutsu.getId("shadow_step_damage"));

        Buff.createBuff(player, ModAttributes.INVINCIBLE, ImmutableList.of(new TimeCancellingCondition(30)), Buff.CancellingPolicy.ONE_OR_MORE, 1,
                EntityAttributeModifier.Operation.ADD_VALUE, Jujutsu.getId("shadow_step_invincible"));

        Buff.createBuff(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, ImmutableList.of(new TimeCancellingCondition(30)), Buff.CancellingPolicy.ONE_OR_MORE, 2.5,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, Jujutsu.getId("shadow_step_speed"));

        player.playSoundToPlayer(ModSounds.SHADOW_STEP_CAST, SoundCategory.MASTER, 2, 1.1f);

        VisualEffectUtils.sendColorModifier((ServerPlayerEntity) player, 5, 15, 10, 0.5f, 0.25f,0x320032);
    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {

    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {
        if(player.getWorld().isClient()) return;

        player.playSoundToPlayer(ModSounds.SHADOW_STEP_END, SoundCategory.MASTER, 2, 1.1f);
    }

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstance instance) {
        return instance.getUseTime() >= 30;
    }

    public static void renderHud(DrawContext context, RenderTickCounter counter, AbilityInstance instance) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        float alpha;
        if(instance.getUseTime() >= 20) {
            alpha = MathHelper.clampedLerp(0.25f, 0, (float) (instance.getUseTime() - 20 ) / 20);
        }
        else {
            alpha = MathHelper.clampedLerp(0, 0.25f, (float) instance.getUseTime() / 10);
        }

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        context.setShaderColor(0.1F, 0.1F, 0.1F, alpha);
        context.drawTexture(Identifier.ofVanilla("textures/misc/vignette.png"), 0, 0, -90, 0.0F, 0.0F, context.getScaledWindowWidth(), context.getScaledWindowHeight(), context.getScaledWindowWidth(), context.getScaledWindowHeight());
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();

        context.setShaderColor(1.0F, 1F, 1.0F, 1f);
    }
}
