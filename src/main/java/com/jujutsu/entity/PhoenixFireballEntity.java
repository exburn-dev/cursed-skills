package com.jujutsu.entity;

import com.jujutsu.registry.ModEffects;
import com.jujutsu.registry.ModEntityTypes;
import com.jujutsu.registry.ModSounds;
import com.jujutsu.util.VisualEffectUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PhoenixFireballEntity extends Entity {
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> OWNER;

    public PhoenixFireballEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public PhoenixFireballEntity(Level level, LivingEntity owner) {
        super(ModEntityTypes.PHOENIX_FIREBALL, level);
        entityData.set(OWNER, Optional.of(EntityReference.of(owner)));
    }

    @Override
    public void tick() {
        super.tick();

        double speed = 1.25;
        Vec3d start = this.getPos();
        Vec3d motion = this.getRotationVector().normalize().multiply(speed);
        Vec3d end = start.add(motion);

        HitResult blockHit = this.getWorld().raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                this
        ));

        boolean hitBlock = blockHit.getType() != HitResult.Type.MISS;
        Vec3d blockPos = hitBlock ? blockHit.getPos() : null;

        Vec3d searchEnd = hitBlock ? blockPos : end;

        Box searchBox = this.getBoundingBox().stretch(motion).expand(1.0D);
        EntityHitResult entityHit = ProjectileUtil.getEntityCollision(
                this.getWorld(),
                this,
                start,
                searchEnd,
                searchBox,
                e -> {
                    if (e == this) return false;
                    if (!e.isAlive()) return false;
                    if (e.isSpectator()) return false;
                    if (getOwner().isPresent() && e.getUuid().equals(getOwner().get())) return false;
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

        if (finalHit != null) {
            if (finalHit.getType() == HitResult.Type.ENTITY) {
                EntityHitResult ehr = (EntityHitResult) finalHit;
                hitEntity(ehr.getEntity());
            } else if (finalHit.getType() == HitResult.Type.BLOCK) {
                explode();
            }
            return;
        }

        this.setVelocity(motion);
        this.updatePosition(end.x, end.y, end.z);

        if(this.age >= 100) {
            kill();
        }

    }

    private void hitEntity(Entity entity) {
        if(getOwner().isPresent() && entity.isPlayer() && entity.getUuid().equals(getOwner().get())) return;

        if(entity instanceof LivingEntity livingEntity) {
            float health = livingEntity.getHealth();
            float damage = health * 0.7f + 2;

            livingEntity.damage(getDamageSources().explosion(this, this), damage);
        }
        getWorld().playSound(this, getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.MASTER, 3, 1);

        showOnHitEffectsToOwner();

        kill();
    }

    private void showOnHitEffectsToOwner() {
        if(getOwner().isEmpty()) return;

        PlayerEntity player = getWorld().getPlayerByUuid(getOwner().get());
        if(player == null) return;

        player.playSoundToPlayer(ModSounds.HIT_IMPACT, SoundCategory.MASTER, 1, 1);

        if(getWorld().isClient()) return;
        VisualEffectUtils.sendScreenFlash((ServerPlayerEntity) player, 3, 5, 5, 0.35f, 0xffffff);
        VisualEffectUtils.sendCrosshairMarkData((ServerPlayerEntity) player, 3, 5, 5, 0xffffff);
    }

    private void explode() {
        double radius = 3;
        List<LivingEntity> entities = getWorld().getEntitiesByClass(
                LivingEntity.class,
                Box.of(getPos(), radius * 2, radius * 2, radius * 2),
                (entity) -> getOwner().isEmpty() || !getOwner().get().equals(entity.getUuid()));

        for(LivingEntity entity: entities) {
            if(entity.distanceTo(this) >= radius) continue;

            entity.addStatusEffect(new StatusEffectInstance(ModEffects.INCINERATION, 60, 0, true, false, false));
            entity.setOnFireForTicks(60);
        }

        //getWorld().createExplosion(this, getX(), getY(), getZ(), 2, true, World.ExplosionSourceType.MOB);

        kill();
    }

    public Optional<UUID> getOwner() {
        return this.dataTracker.get(OWNER_UUID);
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
    protected void readAdditionalSaveData(ValueInput valueInput) {

    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {

    }

    static {
        OWNER = SynchedEntityData.defineId(PhoenixFireballEntity.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
    }
}
