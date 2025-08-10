package com.jujutsu.entity;

import com.jujutsu.Jujutsu;
import com.jujutsu.registry.ModEntityTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
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

        Vec3d velocity = getRotationVector().normalize().multiply(1.25);
        setVelocity(velocity);
        move(MovementType.SELF, velocity);

        if(this.age >= 100) {
            kill();
        }

        HitResult hitResult = ProjectileUtil.getCollision(this, entity -> true);
        if(hitResult.getType() == HitResult.Type.BLOCK) {
            explode();
        }
        else if(hitResult.getType() == HitResult.Type.ENTITY) {
            hitEntity(((EntityHitResult) hitResult).getEntity());
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
