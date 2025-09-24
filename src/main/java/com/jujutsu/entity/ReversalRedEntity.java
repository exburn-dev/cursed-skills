package com.jujutsu.entity;

import com.jujutsu.client.particle.BigColoredSparkParticleEffect;
import com.jujutsu.client.particle.ColoredSparkParticleEffect;
import com.jujutsu.event.server.DelayedTasks;
import com.jujutsu.registry.ModEffects;
import com.jujutsu.registry.ModEntityTypes;
import com.jujutsu.registry.ModSounds;
import com.jujutsu.util.BlockExplosions;
import com.jujutsu.util.ParticleUtils;
import com.jujutsu.util.VisualEffectUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
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
    private static final TrackedData<Float> EXPLOSION_POWER;
    private static final TrackedData<Float> DAMAGE_MULTIPLIER;
    private static final TrackedData<Float> STUN_SECONDS;
    private boolean isCharging = true;

    @Override
    public void tick() {
        super.tick();

        if(isCharging) {
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.85));
        }
        else {

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
                        if (getOwnerUuid().isPresent() && e.getUuid().equals(getOwnerUuid().get())) return false;
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
                explode(finalHit);
                return;
            }

            this.setVelocity(motion);
            this.updatePosition(end.x, end.y, end.z);

            if (getVelocity().length() <= 0.02 && !isCharging) {
                explode(null);
            }
        }
        render();
    }

    private void render() {
        Supplier<ParticleEffect> particle = () -> new BigColoredSparkParticleEffect(2, 0.95f,
                new ColoredSparkParticleEffect.ColorTransition(new Vector3f(1, 0, 0), new Vector3f(1, 0, 0)), 0, 0.1f, 2);
        ParticleUtils.createBall(particle, getPos(), getWorld(), 60, getChargeTime() * 0.0005f, 0);

        if(this.age % 20 == 0) {
            Supplier<ParticleEffect> particle1 = () -> new ColoredSparkParticleEffect(4, 0.9f,
                    new ColoredSparkParticleEffect.ColorTransition(new Vector3f(1, 0, 0), new Vector3f(1, 0.2f, 0)), 0, 0.025f, 20);
            ParticleUtils.createBall(particle1, getPos(), getWorld(), 10, getChargeTime() * 0.0005f, 0.025f);
        }
        //ParticleUtils.createCyl(particle, getPos(), getWorld(), 20, 2.5f, 0.1f);
    }

    private void explode(@Nullable HitResult hitResult) {
        setVelocity(Vec3d.ZERO);

        if(hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) hitResult).getEntity();
            entity.damage(this.getDamageSources().explosion(this, this), 0.6f * getChargeTime());

            int stunSeconds = (int) getStunSeconds();
            if(stunSeconds > 0 && entity instanceof LivingEntity livingEntity) {
                livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.STUN, stunSeconds, 0, true, false ,false));
            }

            showOnHitEffectsToOwner();
        }

        List<Entity> entities = getWorld().getEntitiesByType(TypeFilter.instanceOf(Entity.class), Box.of(getPos(), 14, 14, 14), entity -> entity.getType() != ModEntityTypes.REVERSAL_RED && (getOwnerUuid().isEmpty() || !entity.getUuid().equals(this.getOwnerUuid().get())));
        for(Entity entity: entities) {
            double distance = entity.distanceTo(this);
            if(distance > 7) continue;

            float power = 2 * getChargeTime() * 0.01f;
            Vec3d vec = entity.getPos().subtract(getPos()).normalize().multiply(power, power * 0.5, power);
            vec = new Vec3d(vec.x, Math.abs(vec.y), vec.z);
            entity.addVelocity(vec);

            if(!(entity instanceof LivingEntity livingEntity)) continue;
            livingEntity.damage(this.getDamageSources().explosion(this, this), (1 + 0.4f * getChargeTime()) * getDamageMultiplier());
        }

        if(!getWorld().isClient()) {
            ServerWorld world = (ServerWorld) getWorld();
            world.playSound(this, getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.MASTER, 3, 1.1f);
            world.spawnParticles(ParticleTypes.EXPLOSION, getX(), getY(), getZ(), 1, 0, 0, 0, 0);
            ((ServerWorld) getWorld()).spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, getX(), getY() + 1, getZ(), 20, 2, 2, 2, 0.05);

        }
        else {
            for(int i = 0; i < 10; i++) {
                double angle = 2 * Math.PI * i / 10;
                double x = getX() + Math.cos(angle);
                double y = getY() + 1;
                double z = getZ() + Math.sin(angle);
                getWorld().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, getX(), getY() + 1, getZ(), 0, 0, 0);
            }
        }

        //BlockExplosions.createBlockExplosion(getWorld(), getBlockPos(), 5, state -> random.nextInt(3) == 0);

        getWorld().createExplosion(this, getX(), getY(), getZ(), getExplosionPower(), false, World.ExplosionSourceType.MOB);

        BlockExplosions.applyExplosionConsequences(getPos(), getExplosionPower(), getWorld());

        remove(RemovalReason.KILLED);
    }

    private void showOnHitEffectsToOwner() {
        if(getOwnerUuid().isEmpty() || getWorld().isClient()) return;

        PlayerEntity player = getWorld().getPlayerByUuid(getOwnerUuid().get());
        if(player == null) return;

        player.playSoundToPlayer(ModSounds.HIT_IMPACT, SoundCategory.MASTER, 1, 1);

        if(getWorld().isClient()) return;
        VisualEffectUtils.sendScreenFlash((ServerPlayerEntity) player, 3, 5, 5, 0.35f, 0xffffff);
        VisualEffectUtils.sendCrosshairMarkData((ServerPlayerEntity) player, 3, 5, 5, 0xffffff);
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        if(!isCharging && !state.isAir()) {
            explode(null);
        }
        super.onBlockCollision(state);
    }

    public void increaseChargeTime() {
        this.dataTracker.set(CHARGE_TIME, getChargeTime() + 1);
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

    public float getExplosionPower() {
        return this.dataTracker.get(EXPLOSION_POWER);
    }

    public void setExplosionPower(float value) {
        this.dataTracker.set(EXPLOSION_POWER, value);
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

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(CHARGE_TIME, 0);
        builder.add(OWNER_UUID, Optional.empty());
        builder.add(EXPLOSION_POWER, 1f);
        builder.add(DAMAGE_MULTIPLIER, 1f);
        builder.add(STUN_SECONDS, 0f);
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
        EXPLOSION_POWER = DataTracker.registerData(ReversalRedEntity.class, TrackedDataHandlerRegistry.FLOAT);
        DAMAGE_MULTIPLIER = DataTracker.registerData(ReversalRedEntity.class, TrackedDataHandlerRegistry.FLOAT);
        STUN_SECONDS = DataTracker.registerData(ReversalRedEntity.class, TrackedDataHandlerRegistry.FLOAT);
    }
}
