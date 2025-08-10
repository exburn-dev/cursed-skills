package com.jujutsu.entity;

import com.jujutsu.Jujutsu;
import com.jujutsu.registry.ModEntityTypes;
import com.jujutsu.registry.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public class PhoenixFireballEntity extends Entity {
    private static final TrackedData<Optional<UUID>> OWNER_UUID;

    public PhoenixFireballEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public PhoenixFireballEntity(World world, UUID ownerUuid) {
        super(ModEntityTypes.PHOENIX_FIREBALL, world);
        this.dataTracker.set(OWNER_UUID, Optional.of(ownerUuid));
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

    protected void onCollision(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult ehr = (EntityHitResult) hitResult;
            hitEntity(ehr.getEntity());
        } else if (hitResult.getType() == HitResult.Type.BLOCK) {
            explode();
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

        if(getOwner().isPresent()) {
            PlayerEntity player = getWorld().getPlayerByUuid(getOwner().get());
            if(player != null) {
                player.playSoundToPlayer(ModSounds.HIT_IMPACT, SoundCategory.MASTER, 1, 1);
            }
        }

        kill();
    }

    private void explode() {
        getWorld().playSound(this, getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.MASTER, 3, 1);
        kill();
    }

    public Optional<UUID> getOwner() {
        return this.dataTracker.get(OWNER_UUID);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(OWNER_UUID, Optional.empty());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    static {
        OWNER_UUID = DataTracker.registerData(PhoenixFireballEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    }
}
