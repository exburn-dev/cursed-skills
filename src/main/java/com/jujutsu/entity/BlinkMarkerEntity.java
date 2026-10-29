package com.jujutsu.entity;

import com.jujutsu.registry.ModEntityTypes;
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
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class BlinkMarkerEntity extends Entity {
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> OWNER;

    public BlinkMarkerEntity(EntityType<?> type, Level level) {
        super(type, level);
        setOwnerUUID(null);
    }

    public BlinkMarkerEntity(Level level, LivingEntity owner) {
        super(ModEntityTypes.BLINK_MARKER, level);
        setOwnerUUID(EntityReference.of(owner));
    }

    @Override
    public void tick() {
        super.tick();
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();

        this.applyGravity();
        this.move(MovementType.SELF, this.getVelocity());
        this.setVelocity(this.getVelocity().multiply(0.98));
        if (this.isOnGround()) {
            this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
        }
    }

    public Optional<UUID> getOwnerUUID() {
        return this.dataTracker.get(OWNER_UUID);
    }

    private void setOwnerUUID(@Nullable EntityReference<LivingEntity> entity) {
        entityData.set(OWNER, Optional.ofNullable(entity));
    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public boolean isCollidable() {
        return super.isCollidable();
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
        entityData.set(OWNER, Optional.ofNullable(EntityReference.read(data, "owner")));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput data) {
        EntityReference.store(entityData.get(OWNER).orElse(null), data, "owner");
    }

    static {
        OWNER = SynchedEntityData.defineId(BlinkMarkerEntity.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
    }
}
