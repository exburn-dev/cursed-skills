package com.jujutsu.ability.active;

import com.google.common.collect.ImmutableList;
import com.jujutsu.Jujutsu;
import com.jujutsu.ability.passive.SpeedPassiveAbility;
import com.jujutsu.mixin.LivingEntityAccessor;
import com.jujutsu.registry.ModAbilities;
import com.jujutsu.registry.ModAbilityAttributes;
import com.jujutsu.systems.ability.attribute.SimpleAbilityAttributeContainer;
import com.jujutsu.systems.ability.client.ClientComponentContainer;
import com.jujutsu.systems.ability.core.AbilityInstance;
import com.jujutsu.systems.ability.core.AbilityInstanceData;
import com.jujutsu.systems.ability.data.*;
import com.jujutsu.systems.ability.core.AbilityType;
import com.jujutsu.systems.ability.passive.PassiveAbilityComponent;
import com.jujutsu.systems.buff.Buff;
import com.jujutsu.systems.buff.conditions.TimerBuffPredicate;
import com.jujutsu.systems.buff.type.AttributeBuff;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Optional;

public class SonicRiftAbility extends AbilityType {
    private static final IntAbilityProperty DASHES_LEFT = IntAbilityProperty.of("dashesLeft");
    private static final DoubleAbilityProperty SPEED_ON_START = DoubleAbilityProperty.of("speedOnStart");
    private static final BoolAbilityProperty DASHING = BoolAbilityProperty.of("dashing");
    private static final IntAbilityProperty DASH_DELAY = IntAbilityProperty.of("dashDelay");

    public SonicRiftAbility(int cooldownTime) {
        super(cooldownTime, false, new ClientData(null, SonicRiftAbility::renderHud));
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        PassiveAbilityComponent component = PassiveAbilityComponent.get(player);
        Optional<SpeedPassiveAbility> optional = component.find(ModAbilities.SPEED_PASSIVE_ABILITY);

        double speed = 0;
        int dashes = 0;

        if(optional.isPresent()) {
            SpeedPassiveAbility passiveAbility = optional.get();
            speed = passiveAbility.getDistance();
            float strength = getAbilityStrength(speed);

            dashes = (int) Math.floor(clampValue(strength, 1, 3));
            dashes += (int) getAttributeValue(player, ModAbilityAttributes.SONIC_RIFT_ADDITIONAL_DASHES);
        }

        setData(instance, dashes, speed, false, 0);
        instance.sendToClient();
    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {
        if(player.getWorld().isClient()) return;

        if(instance.get(DASHING)) {
            HitResult hitResult = getPlayerCollision(player);
            boolean hasCollision = hitResult != null;
            boolean hasDelay = instance.get(DASH_DELAY) > 0;

            if(((!hasDelay && player.isOnGround()) || hasCollision)) {
                setData(instance, instance.get(DASHES_LEFT), instance.get(SPEED_ON_START), false, 0);

                setDashingProperties(player, false);
                if(instance.get(DASHES_LEFT) > 0) {
                    addLaunchInput(player, instance);
                }
            }
            else if(hasDelay) {
                setData(instance, instance.get(DASHES_LEFT), instance.get(SPEED_ON_START), true, instance.get(DASH_DELAY) - 1);
            }

            if(hasCollision && hitResult.getType() == HitResult.Type.ENTITY) {
                EntityHitResult ehr = (EntityHitResult) hitResult;
                double damage = getAttributeValue(player, ModAbilityAttributes.SONIC_RIFT_DAMAGE);
                ehr.getEntity().damage(player.getDamageSources().magic(), (float) (damage + instance.get(SPEED_ON_START)));
            }
        }
        else {
            if (instance.useTime() == 0 && instance.get(DASHES_LEFT) > 0) {
                double startJumpMultiplier = getAttributeValue(player, ModAbilityAttributes.SONIC_RIFT_START_JUMP_POWER);
                Vec3d vec = new Vec3d(0f, 0.5f + 1.5f * getAbilityStrength(instance.get(SPEED_ON_START)), 0f).multiply(startJumpMultiplier);
                player.addVelocity(vec);
                player.velocityModified = true;

                player.playSoundToPlayer(SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.MASTER, 1, 1);

                addLaunchInput(player, instance);
            }
        }
    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {
        setDashingProperties(player, false);
    }

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstance instance) {
        return instance.get(DASHES_LEFT) <= 0 && !instance.get(DASHING);
    }

    private float getAbilityStrength(double speed) {
        return (float) Math.min(1f, speed / 10f);
    }

    private float clampValue(float percentage, float min, float max) {
        return min + (max - min) * percentage;
    }

    private HitResult getPlayerCollision(PlayerEntity player) {
        Vec3d start = player.getPos();
        Vec3d motion = player.getVelocity().multiply(5);
        Vec3d end = start.add(motion);

        HitResult blockHit = player.getWorld().raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player
        ));

        boolean hitBlock = blockHit.getType() != HitResult.Type.MISS;
        Vec3d blockPos = hitBlock ? blockHit.getPos() : null;

        Vec3d searchEnd = hitBlock ? blockPos : end;

        Box searchBox = player.getBoundingBox().stretch(motion).expand(5.0D);
        EntityHitResult entityHit = ProjectileUtil.getEntityCollision(
                player.getWorld(),
                player,
                start,
                searchEnd,
                searchBox,
                e -> {
                    if (e == player) return false;
                    if (!e.isAlive()) return false;
                    if (e.isSpectator()) return false;
                    return true;
                }
        );

        HitResult finalHit = null;
        if (entityHit != null) {
            double entityDist2 = start.squaredDistanceTo(entityHit.getPos());
            double blockDist2 = hitBlock ? start.squaredDistanceTo(blockPos) : Double.POSITIVE_INFINITY;
            if (entityDist2 <= blockDist2) {
                finalHit = entityHit;
            } else {
                finalHit = blockHit;
            }
        } else if (hitBlock) {
            finalHit = blockHit;
        }
        return finalHit;
    }

    private void addLaunchInput(PlayerEntity player, AbilityInstance instance) {
        instance.requestInput(InputRequest.mouseRequest(0,
                (player1) -> {
                    launch(player1, instance);
                    return ActionResult.SUCCESS;
                })
                .addTimeout(70)
                .addTimeoutTask(
                        (player1) -> {
                            setData(instance, 0, 0, false, 20);
                            return ActionResult.SUCCESS;
                        }
                )
                .build()
        );
    }

    private void launch(PlayerEntity player, AbilityInstance instance) {
        if(instance.get(DASHES_LEFT) > 0) {
            double dashVelocityMultiplier = getAttributeValue(player, ModAbilityAttributes.SONIC_RIFT_DASH_POWER);

            player.requestTeleport(player.getX(), player.getY() + 1, player.getZ());
            Vec3d vec = player.getRotationVector()
                    .multiply(0.75 + 0.75 * getAbilityStrength(instance.get(SPEED_ON_START)))
                    .multiply(dashVelocityMultiplier);
            player.addVelocity(vec);
            player.velocityModified = true;

            player.playSoundToPlayer(SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST.value(), SoundCategory.MASTER, 1, 1.25f);

            setData(instance, instance.get(DASHES_LEFT) - 1, instance.get(SPEED_ON_START), true, 20);
            //instance.sync();
            instance.sendToClient();
        }
        if(!player.getWorld().isClient()) {
            setDashingProperties(player, true);
        }
    }

    private void setDashingProperties(PlayerEntity player, boolean isDashing) {
        setPlayerUsingRiptide(player, isDashing);

        if(isDashing) {
            AttributeBuff buff = new AttributeBuff(EntityAttributes.GENERIC_GRAVITY, -0.75, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            Buff.createBuff(player, buff, ImmutableList.of(new TimerBuffPredicate(40)),
                    false, Jujutsu.id("sonicrift_gravity"));
        }
    }

    private void setPlayerUsingRiptide(PlayerEntity player, boolean value) {
        ((LivingEntityAccessor) player).invokeSetLivingFlag(4, value);
    }

    private void setData(AbilityInstance instance, int dashesLeft, double speedOnStart, boolean dashing, int dashDelay) {
        instance.set(DASHES_LEFT, dashesLeft);
        instance.set(SPEED_ON_START, speedOnStart);
        instance.set(DASHING, dashing);
        instance.set(DASH_DELAY, dashDelay);
    }

    @Override
    public Style getStyle() {
        return Style.EMPTY.withColor(0x3869c9);
    }

    @Override
    public SimpleAbilityAttributeContainer getDefaultAttributes() {
        return SimpleAbilityAttributeContainer.builder()
                .addBaseModifier(ModAbilityAttributes.SONIC_RIFT_DASH_POWER, 1)
                .addBaseModifier(ModAbilityAttributes.SONIC_RIFT_START_JUMP_POWER, 1)
                .addBaseModifier(ModAbilityAttributes.SONIC_RIFT_ADDITIONAL_DASHES, 0)
                .addBaseModifier(ModAbilityAttributes.SONIC_RIFT_DAMAGE, 0)
                .build();
    }

    public static void renderHud(DrawContext context, RenderTickCounter counter, AbilityInstanceData instance) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        AbilityPropertiesContainer properties = ClientComponentContainer.abilityComponent.getRuntimeData(instance.slot());
        int dashesLeft = properties.get(DASHES_LEFT) == null ? 0 : properties.get(DASHES_LEFT);

        MatrixStack matrices = context.getMatrices();
        matrices.push();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();

        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();
        int x = width / 2;
        int y = height / 2 + 10;

        float scale = 0.75f;
        matrices.translate(x * (1f - scale), y * (1f - scale), 0);
        matrices.scale(scale, scale, 1);

        context.drawCenteredTextWithShadow(
                client.textRenderer,
                String.valueOf(dashesLeft),
                x,
                y,
                0x6d93cf);

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();

        matrices.pop();
    }
}
