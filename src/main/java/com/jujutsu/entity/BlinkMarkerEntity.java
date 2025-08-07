package com.jujutsu.entity;

import com.jujutsu.Jujutsu;
import com.jujutsu.registry.ModEntityTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public class BlinkMarkerEntity extends Entity {
    private static final TrackedData<Optional<UUID>> OWNER_UUID;

    public BlinkMarkerEntity(EntityType<?> type, World world) {
        super(type, world);
        setOwnerUUID(Optional.empty());
    }

    public BlinkMarkerEntity(World world, UUID owner) {
        super(ModEntityTypes.BLINK_MARKER, world);
        setOwnerUUID(Optional.of(owner));
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

    private void setOwnerUUID(Optional<UUID> uuid) {
        this.dataTracker.set(OWNER_UUID, uuid);
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
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(OWNER_UUID, Optional.empty());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if(nbt.contains("ownerUUID")) {
            setOwnerUUID(Optional.of(nbt.getUuid("ownerUUID")));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if(getOwnerUUID().isPresent()) {
            nbt.putUuid("ownerUUID", getOwnerUUID().get());
        }
    }

    static {
        OWNER_UUID = DataTracker.registerData(BlinkMarkerEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    }
}
