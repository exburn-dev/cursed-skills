package com.jujutsu.entity;

import com.jujutsu.client.particle.ColoredSparkParticleEffect;
import com.jujutsu.registry.ModEffects;
import com.jujutsu.registry.ModEntityTypes;
import com.jujutsu.util.ParticleUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Supplier;

public class LapseBlueEntity extends Entity {
    public final AnimationState defaultAnimationState = new AnimationState();
    private int defaultAnimationTimeout = 0;

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID;
    private static final EntityDataAccessor<Integer> AGE;
    private static final EntityDataAccessor<Integer> CHARGE_TIME;
    private static final EntityDataAccessor<Boolean> IS_CHARGING;
    private static final EntityDataAccessor<Float> DAMAGE_MULTIPLIER;
    private static final EntityDataAccessor<Float> STUN_SECONDS;

    public LapseBlueEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public LapseBlueEntity(Level level, UUID ownerUuid) {
        super(ModEntityTypes.LAPSE_BLUE, level);
        entityData.set(OWNER_UUID, Optional.of(ownerUuid));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER_UUID, Optional.empty());
        builder.define(AGE, 0);
        builder.define(CHARGE_TIME, 0);
        builder.define(IS_CHARGING, true);
        builder.define(DAMAGE_MULTIPLIER, 1f);
        builder.define(STUN_SECONDS, 0f);
    }

    @Override
    public void tick() {
        super.tick();
        this.move(MovementType.SELF, this.getVelocity());
        this.setVelocity(this.getVelocity().multiply(0.9));
        if (this.isOnGround()) {
            this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
        }

        render();

        if(this.dataTracker.get(AGE) >= 190) {
            this.remove(RemovalReason.KILLED);
        }

        this.dataTracker.set(AGE, this.dataTracker.get(AGE) + 1);

        if(getIsCharging()) {
            setChargeTime(getChargeTime() + 1);
            return;
        }

        float radius = 7f / 70f * getChargeTime();

        List<Entity> entities = getWorld().getEntitiesByType(TypeFilter.instanceOf(Entity.class), Box.of(getPos(), radius * 2, radius * 2, radius * 2),
                entity -> entity.getType() != ModEntityTypes.LAPSE_BLUE && entity.getType() != ModEntityTypes.REVERSAL_RED && entity.getType() != ModEntityTypes.HOLLOW_PURPLE && (getOwnerUuid().isEmpty() || !entity.getUuid().equals(this.getOwnerUuid().get())));
        List<LivingEntity> toDamage = new ArrayList<>();
        for(Entity entity: entities) {
            double distance = entity.distanceTo(this);
            if(distance > radius) continue;
            if(distance < 0.2) {
                distance = 0;
            }

            Vec3d vec = getPos().subtract(entity.getPos()).normalize().multiply(distance * 0.1);
            entity.addVelocity(vec);

            if(distance <= 1 && entity instanceof LivingEntity livingEntity) {
                toDamage.add(livingEntity);
            }
        }

        for(LivingEntity entity: toDamage) {
            float damage = (toDamage.size() - 1) * 3.5f * getDamageMultiplier();
            if(damage <= 0) continue;

            entity.damage(this.getDamageSources().magic(), damage );
            int stunSeconds = (int) getStunSeconds();
            if(stunSeconds > 0) {
                entity.addStatusEffect(new StatusEffectInstance(ModEffects.STUN, stunSeconds, 0, true, false, false));
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float f) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {

    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {

    }

    public void setCharging(boolean charging) {
        dataTracker.set(IS_CHARGING, charging);
    }

    public boolean getIsCharging() {
        return dataTracker.get(IS_CHARGING);
    }

    public void setChargeTime(int value) {
        dataTracker.set(CHARGE_TIME, value);
    }

    public int getChargeTime() {
        return dataTracker.get(CHARGE_TIME);
    }

    public float getDamageMultiplier() {
        return this.dataTracker.get(DAMAGE_MULTIPLIER);
    }

    public void setDamageMultiplier(float value) {
        this.dataTracker.set(DAMAGE_MULTIPLIER, value);
    }

    public float getStunSeconds() {
        return this.dataTracker.get(STUN_SECONDS);
    }

    public void setStunSeconds(float value) {
        this.dataTracker.set(STUN_SECONDS, value);
    }

    private void render() {
        Vector3f color = new Vector3f(0.1f, 0.1f, 1);
        Supplier<ParticleEffect> particle = () -> new ColoredSparkParticleEffect(3, 0.98f,
                new ColoredSparkParticleEffect.ColorTransition(color, color), 0, 0.1f, 2);

        if(this.age % 50 == 0 && !getIsCharging()) {
            Vector3f color1 = new Vector3f(0.25f, 0.25f, 1);
            Supplier<ParticleEffect> particle1 = () -> new ColoredSparkParticleEffect(13, 0.95f,
                    new ColoredSparkParticleEffect.ColorTransition(color1, new Vector3f(0.5f, 0, 1)), 0, 0.1f, 50);

            ParticleUtils.createCyl(particle1, getPos(), getWorld(), 20, 7f / 70 * getChargeTime(), -0.1f);
        }

        if(this.age % 20 == 0) {
            Vector3f color1 = new Vector3f(0.25f, 0.25f, 1);
            Supplier<ParticleEffect> particle1 = () -> new ColoredSparkParticleEffect(8, 0.95f,
                    new ColoredSparkParticleEffect.ColorTransition(color1, color1), 0, 0.1f, 50);

            ParticleUtils.createBall(particle1, getPos(), getWorld(), 10, 4f / 70f * getChargeTime(), -0.1f);
        }

        ParticleUtils.createBallSurface(particle, getPos(), getWorld(), 40, 0.75f / 70f * getChargeTime(), 0);
    }

    public Optional<UUID> getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID);
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    static {
        AGE = DataTracker.registerData(LapseBlueEntity.class, TrackedDataHandlerRegistry.INTEGER);
        OWNER_UUID = DataTracker.registerData(LapseBlueEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
        CHARGE_TIME = DataTracker.registerData(LapseBlueEntity.class, TrackedDataHandlerRegistry.INTEGER);
        IS_CHARGING = DataTracker.registerData(LapseBlueEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        DAMAGE_MULTIPLIER = DataTracker.registerData(LapseBlueEntity.class, TrackedDataHandlerRegistry.FLOAT);
        STUN_SECONDS = DataTracker.registerData(LapseBlueEntity.class, TrackedDataHandlerRegistry.FLOAT);
    }
}
