package com.jujutsu.entity.core;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.*;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public abstract class JujutsuProjectile extends Entity {
    public static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> OWNER;

    public double speed = 1.25;

    public JujutsuProjectile(EntityType<?> entityType, Level level) {
        super(entityType, level);
        setOwner(Optional.empty());
    }

    @Override
    public void tick() {
        baseTick();
        tickCollision(getMotion());
    }

    public HitResult tickCollision(Vec3 motion) {
        HitResult collision = checkForAnyCollision(motion);
        processResult(collision);
        return collision;
    }

    public HitResult checkForAnyCollision(Vec3 motion) {
        Vec3 start = position();
        Vec3 end = start.add(motion);

        BlockHitResult blockHit = checkForBlockCollision(start, end);
        boolean hitBlock = blockHit.getType() != HitResult.Type.MISS;

        Vec3 entitySearchEnd = hitBlock ? blockHit.getLocation() : end;
        EntityHitResult entityHit = checkForEntityCollision(start, entitySearchEnd, motion);

        HitResult finalHit = null;
        if (entityHit != null && hitBlock) {
            finalHit = getClosestHit(start, entityHit, blockHit);
        } else {
            finalHit = entityHit != null ? entityHit : blockHit;
        }

        return finalHit;
    }

    public BlockHitResult checkForBlockCollision(Vec3 start, Vec3 end) {
        return level().clip(new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                this
        ));
    }

    public EntityHitResult checkForEntityCollision(Vec3 start, Vec3 end, Vec3 motion) {
        AABB searchBox = getBoundingBox().expandTowards(motion).inflate(1.0);
        return ProjectileUtil.getEntityHitResult(
                level(),
                this,
                start,
                end,
                searchBox,
                e -> {
                    if (e == this) return false;
                    if (!e.isAlive()) return false;
                    if (e.isSpectator()) return false;
                    if (getOwner().isPresent() && e.getUUID().equals(getOwner().get().getUUID())) return false;
                    return true;
                },
                0.0f // inflates searchBox, done manually
        );
    }

    private void processResult(HitResult result) {
        if(!isHitSomething(result)) return;
        if(result.getType() == HitResult.Type.ENTITY) {
            onEntityHit((EntityHitResult) result);
        }
        else if(result.getType() == HitResult.Type.BLOCK) {
            onBlockHit((BlockHitResult) result);
        }
        onAnyCollision(result);
    }

    public void onEntityHit(EntityHitResult hitResult) { }
    public void onBlockHit(BlockHitResult hitResult) { }
    public void onAnyCollision(HitResult hitResult) { }

    public Vec3 getMotion() {
        return Vec3.directionFromRotation(getRotationVector()).normalize().scale(speed);
    }

    public void hurtIfServerside(Entity entity, DamageSource damageSource, float amount) {
        if(level().isClientSide()) return;
        entity.hurtServer((ServerLevel) level(), damageSource, amount);
    }

    public boolean isOtherEntityType(Entity entity) {
        return entity.getType() != getType();
    }

    public boolean isNotOwner(Entity entity) {
        Optional<EntityReference<LivingEntity>> owner = getOwner();
        return owner.isEmpty() || !entity.getUUID().equals(owner.get().getUUID());
    }

    @Nullable
    public LivingEntity getOwnerEntity() {
        return EntityReference.getLivingEntity(entityData.get(OWNER).orElse(null), level());
    }

    public Optional<EntityReference<LivingEntity>> getOwner() {
        return entityData.get(OWNER);
    }

    protected void setOwner(@Nullable LivingEntity entity) {
        entityData.set(OWNER, entity == null
                ? Optional.empty()
                : Optional.of(EntityReference.of(entity)));
    }

    protected void setOwner(@Nullable EntityReference<LivingEntity> entityRef) {
        entityData.set(OWNER, Optional.ofNullable(entityRef));
    }

    protected void setOwner(Optional<EntityReference<LivingEntity>> entity) {
        entityData.set(OWNER, entity);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER, Optional.empty());
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float f) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput data) {
        setOwner(EntityReference.read(data, "owner"));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput data) {
        EntityReference.store(getOwner().orElse(null), data, "owner");
    }

    public static boolean isHitSomething(HitResult result) {
        return result.getType() != HitResult.Type.MISS;
    }

    public static HitResult getClosestHit(Vec3 to, HitResult first, HitResult second) {
        double distanceFirst = to.distanceToSqr(first.getLocation());
        double distanceSecond = to.distanceToSqr(second.getLocation());
        if(distanceFirst <= distanceSecond) {
            return first;
        }
        else {
            return second;
        }
    }

    static {
        OWNER = SynchedEntityData.defineId(JujutsuProjectile.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
    }
}
