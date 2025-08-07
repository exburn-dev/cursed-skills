package com.jujutsu.entity;

import com.jujutsu.Jujutsu;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PhoenixFireballEntity extends Entity {
    public PhoenixFireballEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();

        Vec3d velocity = getRotationVector().normalize().multiply(2);
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
        if(entity instanceof LivingEntity livingEntity) {
            float health = livingEntity.getHealth();
            float damage = health * 0.7f;

            livingEntity.damage(getDamageSources().explosion(this, this), damage);
        }
        getWorld().playSound(this, getBlockPos(), SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.MASTER, 1, 1);
        kill();
    }

    private void explode() {
        getWorld().playSound(this, getBlockPos(), SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.MASTER, 1, 1);
        kill();
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }
}
