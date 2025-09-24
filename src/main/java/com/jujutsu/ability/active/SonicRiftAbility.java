package com.jujutsu.ability.active;

import com.jujutsu.ability.passive.SpeedPassiveAbility;
import com.jujutsu.mixin.LivingEntityAccessor;
import com.jujutsu.registry.ModAbilityAttributes;
import com.jujutsu.systems.ability.attribute.AbilityAttributesContainer;
import com.jujutsu.systems.ability.data.AbilityAdditionalInput;
import com.jujutsu.systems.ability.data.AbilityData;
import com.jujutsu.systems.ability.core.AbilityInstance;
import com.jujutsu.systems.ability.core.AbilityType;
import com.jujutsu.systems.ability.data.ClientData;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
import com.jujutsu.util.AbilitiesHolderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
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
    public static final Codec<SonicRiftData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("dashesLeft").forGetter(SonicRiftData::dashesLeft),
            Codec.DOUBLE.fieldOf("speedOnStart").forGetter(SonicRiftData::speedOnStart),
            Codec.BOOL.fieldOf("dashing").forGetter(SonicRiftData::dashing),
            Codec.INT.fieldOf("dashDelay").forGetter(SonicRiftData::dashDelay)
    ).apply(instance, SonicRiftData::new));

    public SonicRiftAbility(int cooldownTime) {
        super(cooldownTime, false, new ClientData(null, SonicRiftAbility::renderHud));
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        Optional<SpeedPassiveAbility> optional = AbilitiesHolderUtils.findPassiveAbility((IAbilitiesHolder) player, SpeedPassiveAbility.class);
        double speed = 0;
        int dashes = 0;

        if(optional.isPresent()) {
            SpeedPassiveAbility passiveAbility = optional.get();
            speed = passiveAbility.getDistance();
            float strength = getAbilityStrength(speed);
            if(strength == 1f) {
                dashes = 3;
            }
            else if(strength >= 0.5f) {
                dashes = 2;
            }
            else if(strength >= 0f) {
                dashes = 1;
            }
            dashes += (int) getAbilityAttributeValue(player, ModAbilityAttributes.SONIC_RIFT_ADDITIONAL_DASHES);
        }
        instance.setAbilityData(new SonicRiftData(dashes, speed, false, 0));
    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {
        SonicRiftData data = getData(instance);
        if(data.dashing()) {
            HitResult hitResult = getPlayerCollision(player);
            boolean hasCollision = hitResult != null;
            boolean hasDelay = data.dashDelay() > 0;

            if(((!hasDelay && player.isOnGround()) || hasCollision)) {
                instance.setAbilityData(new SonicRiftData(data.dashesLeft(), data.speedOnStart(), false, 0));

                setPlayerUsingRiptide(player, false);
                if(data.dashesLeft() > 0) {
                    addLaunchInput(player, instance);
                }
            }
            else if(hasDelay) {
                instance.setAbilityData(new SonicRiftData(data.dashesLeft(), data.speedOnStart(), true, data.dashDelay() - 1));
            }

            if(hasCollision && hitResult.getType() == HitResult.Type.ENTITY) {
                EntityHitResult ehr = (EntityHitResult) hitResult;
                double damage = getAbilityAttributeValue(player, ModAbilityAttributes.SONIC_RIFT_DAMAGE);
                ehr.getEntity().damage(player.getDamageSources().magic(), (float) (damage + data.speedOnStart()));
            }
        }
        else {
            if (instance.getUseTime() == 0 && data.dashesLeft() > 0) {
                double startJumpMultiplier = getAbilityAttributeValue(player, ModAbilityAttributes.SONIC_RIFT_START_JUMP_POWER);
                Vec3d vec = new Vec3d(0f, 0.5f + 1.5f * getAbilityStrength(data.speedOnStart()), 0f).multiply(startJumpMultiplier);
                player.addVelocity(vec);
                player.velocityModified = true;

                player.playSoundToPlayer(SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.MASTER, 1, 1);

                addLaunchInput(player, instance);
            }
        }
    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {
        setPlayerUsingRiptide(player, false);
    }

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstance instance) {
        SonicRiftData data = getData(instance);
        return data.dashesLeft() <= 0 && !data.dashing();
    }

    private float getAbilityStrength(double speed) {
        return (float) Math.min(1f, speed / 10f);
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
        instance.addAdditionalInput(player,
                new AbilityAdditionalInput(-1, -1, 0, 70, true),
                (player1) -> {
                    launch(player1, instance);
                    return ActionResult.SUCCESS;
                },
                (player1) -> {
                    instance.setAbilityData(new SonicRiftData(0, 0, false, 20));
                    return ActionResult.SUCCESS;
                });
    }

    private void launch(PlayerEntity player, AbilityInstance instance) {
        SonicRiftData data = getData(instance);
        if(data.dashesLeft() > 0) {
            double dashVelocityMultiplier = getAbilityAttributeValue(player, ModAbilityAttributes.SONIC_RIFT_DASH_POWER);

            player.requestTeleport(player.getX(), player.getY() + 1, player.getZ());
            Vec3d vec = player.getRotationVector()
                    .multiply(0.75 + 0.75 * getAbilityStrength(data.speedOnStart()))
                    .multiply(dashVelocityMultiplier);
            player.addVelocity(vec);
            player.velocityModified = true;

            player.playSoundToPlayer(SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST.value(), SoundCategory.MASTER, 1, 1.25f);

            instance.setAbilityData(new SonicRiftData(data.dashesLeft() - 1, data.speedOnStart(), true, 20));
            instance.sync();
        }
        if(!player.getWorld().isClient()) {
            setPlayerUsingRiptide(player, true);
        }
    }

    private void setPlayerUsingRiptide(PlayerEntity player, boolean value) {
        ((LivingEntityAccessor) player).invokeSetLivingFlag(4, value);
    }

    private SonicRiftData getData(AbilityInstance instance) {
        return instance.getAbilityData(SonicRiftData.class, () -> (SonicRiftData) getInitialData());
    }

    @Override
    public AbilityData getInitialData() {
        return new SonicRiftData(0, 0, false, 0);
    }

    @Override
    public Codec<? extends AbilityData> getCodec() {
        return CODEC;
    }

    @Override
    public Style getStyle() {
        return Style.EMPTY.withColor(0x3869c9);
    }

    @Override
    public AbilityAttributesContainer getDefaultAttributes() {
        return new AbilityAttributesContainer.Builder()
                .addBaseModifier(ModAbilityAttributes.SONIC_RIFT_DASH_POWER, 1)
                .addBaseModifier(ModAbilityAttributes.SONIC_RIFT_START_JUMP_POWER, 1)
                .addBaseModifier(ModAbilityAttributes.SONIC_RIFT_ADDITIONAL_DASHES, 0)
                .addBaseModifier(ModAbilityAttributes.SONIC_RIFT_DAMAGE, 0)
                .build();
    }

    public static void renderHud(DrawContext context, RenderTickCounter counter, AbilityInstance instance) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        SonicRiftData data = instance.getAbilityData(SonicRiftData.class, () -> new SonicRiftData(0, 0, false, 0));
        int dashesLeft = data.dashesLeft();

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

    public record SonicRiftData(int dashesLeft, double speedOnStart, boolean dashing, int dashDelay) implements AbilityData {}
}
