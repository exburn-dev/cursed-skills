package com.jujutsu.entity;

import com.jujutsu.client.particle.ColoredSparkParticleEffect;
import com.jujutsu.entity.core.JujutsuProjectile;
import com.jujutsu.registry.ModEffects;
import com.jujutsu.registry.ModEntityTypes;
import com.jujutsu.registry.ModSounds;
import com.jujutsu.util.BlockExplosions;
import com.jujutsu.util.ParticleUtils;
import com.jujutsu.util.VisualEffectUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ReversalRedEntity extends JujutsuProjectile {
    private static final EntityDataAccessor<Integer> CHARGE_TIME;
    private static final EntityDataAccessor<Float> EXPLOSION_POWER;
    private static final EntityDataAccessor<Float> DAMAGE_MULTIPLIER;
    private static final EntityDataAccessor<Float> STUN_SECONDS;
    private boolean isCharging = true;

    public ReversalRedEntity(EntityType<?> type, Level level) {
        super(type, level);
        entityData.set(OWNER, Optional.empty());
    }

    public ReversalRedEntity(Level level, LivingEntity owner) {
        super(ModEntityTypes.REVERSAL_RED, level);
        entityData.set(OWNER, Optional.of(EntityReference.of(owner)));
    }

    @Override
    public void tick() {
        super.tick();
        baseTick();

        if(isCharging) {
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.85));
        }
        else {
            Vec3 motion = getMotion();
            tickCollision(motion);

            setDeltaMovement(motion);
            moveOrInterpolateTo(position().add(motion));

            if (getDeltaMovement().lengthSqr() <= 0.02*0.02 && !isCharging) {
                explode();
            }
        }

        spawnVisualParticles();
    }

    @Override
    public void onEntityHit(EntityHitResult hitResult) {
        if(isCharging) return;

        Entity entity = hitResult.getEntity();
        hurtIfServerside(entity, this.damageSources().explosion(this, this), 0.6f * getChargeTime());

        int stunSeconds = (int) getStunSeconds();
        if(stunSeconds > 0 && entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(ModEffects.STUN, stunSeconds, 0, true, false ,false));
        }

        showOnHitEffectsToOwner();
    }

    @Override
    public void onAnyCollision(HitResult hitResult) {
        if(isCharging) return;

        setDeltaMovement(Vec3.ZERO);

        explode();
    }

    public void explode() {
        List<Entity> entities = level().getEntities(
                EntityTypeTest.forClass(Entity.class),
                AABB.ofSize(position(), 14, 14, 14),
                e -> isOtherEntityType(e) && isNotOwner(e));

        explodeEntities(entities);
        spawnExplosionParticles();
        BlockExplosions.applyExplosionConsequences(position(), getExplosionPower(), level());

        //BlockExplosions.createBlockExplosion(getWorld(), getBlockPos(), 5, state -> random.nextInt(3) == 0);
        //getWorld().createExplosion(this, getX(), getY(), getZ(), getExplosionPower(), false, World.ExplosionSourceType.MOB);

        remove(RemovalReason.KILLED);
    }

    private void explodeEntities(List<Entity> entities) {
        for(Entity entity: entities) {
            double distance = entity.distanceTo(this);
            if(distance > 7) continue;

            float power = 2 * getChargeTime() * 0.01f;
            Vec3 knockbackVec = entity.position().subtract(position()).normalize().multiply(power, power * 0.5, power);
            knockbackVec = knockbackVec.with(Direction.Axis.Y, Math.abs(knockbackVec.y));
            entity.addDeltaMovement(knockbackVec);

            if(!(entity instanceof LivingEntity livingEntity)) continue;

            hurtIfServerside(livingEntity, damageSources().explosion(this, this), getExplosionDamage());
        }
    }

    private void spawnExplosionParticles() {
        if(!level().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) level();
            serverLevel.playSound(this, blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.MASTER, 3, 1.1f);
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, getX(), getY(), getZ(), 1, 0, 0, 0, 0);
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, getX(), getY() + 1, getZ(), 20, 2, 2, 2, 0.05);
            return;
        }

        for(int i = 0; i < 10; i++) {
            double angle = 2 * Math.PI * i / 10;
            double x = getX() + Math.cos(angle);
            double y = getY() + 1;
            double z = getZ() + Math.sin(angle);
            level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 0, 0, 0);
        }
    }

    private void spawnVisualParticles() {
        Supplier<ParticleOptions> particle = () -> ColoredSparkParticleEffect.create()
                .scale(2, 0.95f)
                .color(1, 0, 0)
                .roll(0, 0.1f)
                .lifetime(2)
                .buildBig();
        ParticleUtils.createBall(particle, position(), level(), 60, getChargeTime() * 0.0005f, 0);

        if(this.tickCount % 20 == 0) {
            Supplier<ParticleOptions> particle1 = () -> ColoredSparkParticleEffect.create()
                    .scale(4.0f, 0.9f)
                    .color(1, 0, 0, 1, 0.2f, 0)
                    .roll(0, 0.025f)
                    .lifetime(20)
                    .build();
            ParticleUtils.createBall(particle1, position(), level(), 10, getChargeTime() * 0.0005f, 0.025f);
        }
        //ParticleUtils.createCyl(particle, getPos(), getWorld(), 20, 2.5f, 0.1f);
    }

    private void showOnHitEffectsToOwner() {
        if(getOwner().isEmpty() || level().isClientSide()) return;

        LivingEntity entity = getOwnerEntity();
        if(!(entity instanceof ServerPlayer player)) return;

        player.playSound(ModSounds.HIT_IMPACT, 1, 1);

        VisualEffectUtils.sendScreenFlash(player, 3, 5, 5, 0.35f, 0xffffff);
        VisualEffectUtils.sendCrosshairMarkData(player, 3, 5, 5, 0xffffff);
    }

    public float getExplosionDamage() {
        return 1 + 0.4f * getChargeTime() * getDamageMultiplier();
    }

    public void increaseChargeTime() {
        entityData.set(CHARGE_TIME, getChargeTime() + 1);
    }

    public void setCharging(boolean charging) {
        isCharging = charging;
    }

    public int getChargeTime() {
        return entityData.get(CHARGE_TIME);
    }

    public float getExplosionPower() {
        return entityData.get(EXPLOSION_POWER);
    }

    public void setExplosionPower(float value) {
        entityData.set(EXPLOSION_POWER, value);
    }

    public float getDamageMultiplier() {
        return entityData.get(DAMAGE_MULTIPLIER);
    }

    public void setDamageMultiplier(float value) {
        entityData.set(DAMAGE_MULTIPLIER, value);
    }

    public float getStunSeconds() {
        return entityData.get(STUN_SECONDS);
    }

    public void setStunSeconds(float value) {
        entityData.set(STUN_SECONDS, value);
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float f) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CHARGE_TIME, 0);
        builder.define(EXPLOSION_POWER, 1f);
        builder.define(DAMAGE_MULTIPLIER, 1f);
        builder.define(STUN_SECONDS, 0f);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput data) {
        super.readAdditionalSaveData(data);
        entityData.set(OWNER, Optional.ofNullable(EntityReference.read(data, "owner")));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput data) {
        super.addAdditionalSaveData(data);
        EntityReference.store(entityData.get(OWNER).orElse(null), data, "owner");
    }

    static {
        CHARGE_TIME = SynchedEntityData.defineId(ReversalRedEntity.class, EntityDataSerializers.INT);
        EXPLOSION_POWER = SynchedEntityData.defineId(ReversalRedEntity.class, EntityDataSerializers.FLOAT);
        DAMAGE_MULTIPLIER = SynchedEntityData.defineId(ReversalRedEntity.class, EntityDataSerializers.FLOAT);
        STUN_SECONDS = SynchedEntityData.defineId(ReversalRedEntity.class, EntityDataSerializers.FLOAT);
    }
}
