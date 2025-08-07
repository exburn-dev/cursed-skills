package com.jujutsu.ability.active;

import com.google.common.collect.ImmutableList;
import com.jujutsu.Jujutsu;
import com.jujutsu.network.payload.SyncPlayerAbilitiesPayload;
import com.jujutsu.systems.ability.AbilityInstance;
import com.jujutsu.systems.ability.AbilityType;
import com.jujutsu.systems.ability.ClientData;
import com.jujutsu.systems.ability.IPlayerJujutsuAbilitiesHolder;
import com.jujutsu.systems.buff.Buff;
import com.jujutsu.systems.buff.conditions.AttackCancellingCondition;
import com.jujutsu.systems.buff.conditions.TimeCancellingCondition;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

public class FlashPunchAbility extends AbilityType {
    private static final int MAX_DISTANCE = 50;

    public FlashPunchAbility(int cooldownTime) {
        super(cooldownTime, false, new ClientData(null, FlashPunchAbility::renderHud));
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        instance.getNbt().putFloat("startDistance", player.distanceTraveled);
    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {
        if(!player.getWorld().isClient()) {
            float startDistance = instance.getNbt().getFloat("startDistance");
            instance.getNbt().putFloat("distance", Math.clamp(player.distanceTraveled - startDistance, 0, MAX_DISTANCE));

            ServerPlayNetworking.send((ServerPlayerEntity) player, new SyncPlayerAbilitiesPayload(((IPlayerJujutsuAbilitiesHolder) player).getAbilities()));
        }
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 2, 2, true, false, false));
    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {
        double damageBonus = instance.getNbt().getInt("distance") * 0.8;
        Buff.createBuff(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, ImmutableList.of(new TimeCancellingCondition(500), new AttackCancellingCondition(player.getLastAttackTime())), Buff.CancellingPolicy.ONE_OR_MORE, damageBonus,
                EntityAttributeModifier.Operation.ADD_VALUE, Jujutsu.getId("flash_punch_damage"));

        instance.getNbt().remove("startDistance");
        instance.getNbt().remove("distance");
    }

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstance instance) {
        return instance.getUseTime() >= 200 || instance.getNbt().getInt("distance") >= MAX_DISTANCE;
    }

    @Override
    public Style getStyle() {
        return Style.EMPTY.withColor(Formatting.AQUA);
    }

    public static void renderHud(DrawContext context, RenderTickCounter counter, AbilityInstance instance) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;


        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        //context.drawTexture(Identifier.ofVanilla("textures/misc/vignette.png"), 0, 0, -90, 0.0F, 0.0F, context.getScaledWindowWidth(), context.getScaledWindowHeight(), context.getScaledWindowWidth(), context.getScaledWindowHeight());

        float distance = instance.getNbt().getFloat("distance");

        int width = 40;
        int height = 10;
        int x = context.getScaledWindowWidth() / 2 - width / 2;

        float progress = ((float) width / MAX_DISTANCE) * distance;
        context.fill(x, context.getScaledWindowHeight() - 40 - height, x + width, context.getScaledWindowHeight() - 40, 1, 0xFFFFFFFF);
        context.fill(x, context.getScaledWindowHeight() - 40 - height, (int) (x + progress), context.getScaledWindowHeight() - 40, 2, 0xFFFF0000);

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }
}
