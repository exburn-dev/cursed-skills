package com.jujutsu.entity;

import com.jujutsu.client.particle.ColoredSparkParticleEffect;
import com.jujutsu.registry.ModEntityTypes;
import com.jujutsu.util.ParticleUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class ReversalRedEntity extends Entity {
    public ReversalRedEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public ReversalRedEntity(World world, UUID ownerUUID) {
        super(ModEntityTypes.REVERSAL_RED, world);
        this.dataTracker.set(OWNER_UUID, Optional.of(ownerUUID));
    }

    private static final TrackedData<Optional<UUID>> OWNER_UUID;
    private static final TrackedData<Integer> CHARGE_TIME;
    private boolean isCharging = true;
    private int explosionTicks = 0;

    @Override
    public void tick() {
        super.tick();
        if(explosionTicks > 0) {
            explode();
            return;
        }

        this.move(MovementType.SELF, this.getVelocity());
        this.setVelocity(this.getVelocity().multiply(isCharging ? 0.8 : 0.9));
        if (this.isOnGround()) {
            this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
        }

        render();
        if(getWorld().isClient()) return;

        if(isCharging) {
            this.dataTracker.set(CHARGE_TIME, getChargeTime() + 1);
        }

        if(getVelocity().length() <= 0.02 && !isCharging) {
            explode();
        }

        HitResult result = ProjectileUtil.getCollision(this, entity -> (getOwnerUuid().isEmpty() || entity.getUuid() != getOwnerUuid().get()));
        if(result.getType() != HitResult.Type.MISS) {
            explode();
        }
    }

    private void render() {
        Supplier<ParticleEffect> particle = () -> new ColoredSparkParticleEffect(2, 0.95f,
                new ColoredSparkParticleEffect.ColorTransition(new Vector3f(1, 0, 0), new Vector3f(1, 0, 0)), 0, 0.1f, 2);
        ParticleUtils.createBall(particle, getPos(), getWorld(), 60, getChargeTime() * 0.005f, 0);

        if(this.age % 20 == 0) {
            Supplier<ParticleEffect> particle1 = () -> new ColoredSparkParticleEffect(4, 0.95f,
                    new ColoredSparkParticleEffect.ColorTransition(new Vector3f(1, 0, 0), new Vector3f(1, 0, 0)), 0, 0.1f, 20);
            ParticleUtils.createBall(particle1, getPos(), getWorld(), 10, getChargeTime() * 0.02f, -0.01f);
        }
        //ParticleUtils.createCyl(particle, getPos(), getWorld(), 20, 2.5f, 0.1f);
    }

    private void explode() {
        explosionTicks++;
        setVelocity(this.getVelocity().multiply(0.2));

        List<Entity> entities = getWorld().getEntitiesByType(TypeFilter.instanceOf(Entity.class), Box.of(getPos(), 14, 14, 14), entity -> entity.getType() != ModEntityTypes.REVERSAL_RED && (getOwnerUuid().isEmpty() || !entity.getUuid().equals(this.getOwnerUuid().get())));
        if(explosionTicks < 15) {
            for(Entity entity: entities) {
                double distance = entity.distanceTo(this);
                if(distance > 7 || entity.getUuid() == getOwnerUuid().get()) continue;
                if(distance < 0.2) {
                    distance = 0;
                }

                Vec3d vec = getPos().subtract(entity.getPos()).normalize().multiply(distance * 0.075);
                entity.addVelocity(vec);
            }
        }
        else {
            for(Entity entity: entities) {
                double distance = entity.distanceTo(this);
                if(distance > 7) continue;

                float power = 2 * getChargeTime() * 0.01f;
                Vec3d vec = entity.getPos().subtract(getPos()).normalize().multiply(power, power * 0.5, power);
                vec = new Vec3d(vec.x, Math.abs(vec.y), vec.z);
                entity.addVelocity(vec);
                entity.damage(this.getDamageSources().explosion(this, this), 1 + (40f / 100f) * getChargeTime());
            }
            remove(RemovalReason.KILLED);
        }
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        if(!isCharging && !state.isAir()) {
            explode();
        }
        super.onBlockCollision(state);
    }

    public Optional<UUID> getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID);
    }

    public void setCharging(boolean charging) {
        isCharging = charging;
    }

    public int getChargeTime() {
        return dataTracker.get(CHARGE_TIME);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(CHARGE_TIME, 0);
        builder.add(OWNER_UUID, Optional.empty());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.dataTracker.set(OWNER_UUID, Optional.of(nbt.getUuid("ownerUuid")));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putUuid("ownerUuid", getOwnerUuid().isEmpty() ? UUID.fromString("") : getOwnerUuid().get());
    }

    static {
        OWNER_UUID = DataTracker.registerData(ReversalRedEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
        CHARGE_TIME = DataTracker.registerData(ReversalRedEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }
}
