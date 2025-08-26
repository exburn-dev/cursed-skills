package com.jujutsu.entity;

import com.jujutsu.client.particle.ColoredSparkParticleEffect;
import com.jujutsu.registry.ModDamageTypes;
import com.jujutsu.registry.ModEntityTypes;
import com.jujutsu.util.ParticleUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Supplier;

public class HollowPurpleEntity extends Entity {
    public static final int MAX_AGE = 150;
    public static final double SPEED = 0.75;
    //public static final int BLOCK_BREAK_RADIUS = 3;

    private static final TrackedData<Optional<UUID>> OWNER_UUID;
    private static final TrackedData<Integer> AGE;
    private static final TrackedData<Float> BLOCK_BREAK_RADIUS;

    public HollowPurpleEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public HollowPurpleEntity(World world, UUID ownerUUID) {
        super(ModEntityTypes.HOLLOW_PURPLE, world);
        this.dataTracker.set(OWNER_UUID, Optional.of(ownerUUID));
    }

    @Override
    public void tick() {
        super.tick();

        if(getWorld().isClient()) {
            Vec3d pos = getPos();
            Vec3d vec = getRotationVector().multiply(SPEED);
            Supplier<ParticleEffect> effect = () -> new ColoredSparkParticleEffect(
                    4, 0.8f,
                    new ColoredSparkParticleEffect.ColorTransition(new Vector3f(0.5f, 0, 0.75f), new Vector3f(1, 0, 0)),
                    0, 2f,
                    1
            );
            ParticleUtils.createBallSurface(effect, pos, getWorld(), 100, 2, 0.05f);

            return;
        }

        List<Entity> entities = getWorld().getEntitiesByType(TypeFilter.instanceOf(Entity.class), Box.of(this.getPos(), 12f, 12f, 12f), (entity -> entity.getType() != ModEntityTypes.HOLLOW_PURPLE && (getOwner().isEmpty() || !entity.getUuid().equals(getOwner().get()))));

        move(MovementType.SELF, getRotationVector().normalize().multiply(SPEED));

        BlockBox box = new BlockBox(this.getBlockPos()).expand((int) getBlockBreakRadius());
        Iterator<BlockPos> iterator = BlockPos.iterate(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ()).iterator();
        ServerWorld world = (ServerWorld) getWorld();
        iterator.forEachRemaining(pos -> {
            if(pos.isWithinDistance(this.getPos(), getBlockBreakRadius())) {
                world.breakBlock(pos, false);
            }
        });

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            float distance = entity.distanceTo(this);
            if(distance <= 6f) {
                entity.damage(getDamageSource(world), 99999999);
            }
            else {
                Vec3d vec = getPos().subtract(entity.getPos()).normalize().multiply(0.1f * distance);
                entity.addVelocity(vec);
            }
        }

        if(getAge() >= MAX_AGE) {
            this.remove(RemovalReason.DISCARDED);
        }

        this.dataTracker.set(AGE, getAge() + 1);
    }

    @Override
    public void onRemoved() {
        Supplier<ParticleEffect> effect = () -> new ColoredSparkParticleEffect(
                5, 0.95f,
                new ColoredSparkParticleEffect.ColorTransition(new Vector3f(0.5f, 0, 0.75f), new Vector3f(1, 0, 0)),
                0, 0.2f,
                100 + random.nextInt(50)
        );
        ParticleUtils.createBall(effect, getPos(), getWorld(), 100, 2.25f, 0.15f);
        super.onRemoved();
    }

    public Optional<UUID> getOwner() {
        return this.dataTracker.get(OWNER_UUID);
    }

    public int getAge() {
        return this.dataTracker.get(AGE);
    }

    public float getBlockBreakRadius() {
        return this.dataTracker.get(BLOCK_BREAK_RADIUS);
    }

    public void setBlockBreakRadius(float value) {
        this.dataTracker.set(BLOCK_BREAK_RADIUS, value);
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    protected double getGravity() {
        return 0;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(AGE, 0);
        builder.add(OWNER_UUID, Optional.empty());
        builder.add(BLOCK_BREAK_RADIUS, 0f);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    private DamageSource getDamageSource(ServerWorld serverWorld) {
        Optional<UUID> ownerUUID = getOwner();
        return ownerUUID.map(
                value -> new DamageSource(serverWorld.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.HOLLOW_PURPLE), this, getWorld().getPlayerByUuid(value)))
                .orElseGet(() -> new DamageSource(serverWorld.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.HOLLOW_PURPLE), this));
    }

    static {
        AGE = DataTracker.registerData(HollowPurpleEntity.class, TrackedDataHandlerRegistry.INTEGER);
        OWNER_UUID = DataTracker.registerData(HollowPurpleEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
        BLOCK_BREAK_RADIUS = DataTracker.registerData(HollowPurpleEntity.class, TrackedDataHandlerRegistry.FLOAT);
    }
}
