package com.jujutsu.systems.ability.core;

import com.jujutsu.network.payload.abilities.AbilityRuntimeDataSyncS2CPayload;
import com.jujutsu.systems.ability.data.AbilityPropertiesContainer;
import com.jujutsu.systems.ability.data.AbilityProperty;
import com.jujutsu.systems.ability.data.InputRequest;
import com.jujutsu.systems.entitydata.EntityServerData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;

public class AbilityInstance implements EntityServerData {
    private final PlayerEntity player;
    private AbilityType type;

    private AbilitySlot slot;
    private AbilityStatus status;
    private int useTime;
    private int cooldownTime;

    private AbilityPropertiesContainer runtimeData = new AbilityPropertiesContainer();

    private AbilityInstance(PlayerEntity player) {
        this.player = player;
    }

    public AbilityInstance(PlayerEntity player, AbilityType type, AbilitySlot slot) {
        this.player = player;
        this.type = type;
        this.slot = slot;
        this.status = AbilityStatus.NONE;
    }

    public AbilityInstance(PlayerEntity player, AbilityInstanceData data) {
        this.player = player;
        this.type = data.type();
        this.slot = data.slot();
        this.status = data.status();
        this.useTime = data.useTime();
        this.cooldownTime = data.cooldownTime();
    }

    public void start() {
        if(!status.isNone()) return;

        status = AbilityStatus.RUNNING;
        type.start(player, this);
    }

    public void tick() {
        processStatus();
    }

    public void endAbility() {
        if(!isFinished() && !type.isCancelable()) {
            return;
        }
        else if(!isFinished() && type.isCancelable()) {
            status = AbilityStatus.CANCELLED;
        }

        type.end(player, this);
        status = AbilityStatus.ON_COOLDOWN;
        cooldownTime = type.getCooldownTime(player, this);
        useTime = 0;
        clearInputRequest();
        component().sendToClient();
    }

    public void endCooldown() {
        status = AbilityStatus.NONE;
        cooldownTime = 0;
        useTime = 0;
        component().sendToClient();
    }

    public boolean isFinished() {
        return type.isFinished(player, this);
    }

    private void processStatus() {
        if(status.isRunning()) {
            type.tick(player, this);
            if(isFinished()) {
                endAbility();
            }
            useTime++;
        }
        else if(status.isWaiting()) {
            type.tick(player, this);
        }
        else if(status.onCooldown()) {
            cooldown();
            if(cooldownTime <= 0) {
                endCooldown();
            }
        }
    }

    private void cooldown() {
        if(cooldownTime > 0) cooldownTime--;
    }

    public <T extends Comparable<T>> T get(AbilityProperty<T> property) {
        return runtimeData.get(property);
    }

    public <T extends Comparable<T>> void set(AbilityProperty<T> property, T value) {
        runtimeData.set(property, value);
    }

    public void requestInput(InputRequest request) {
        status = AbilityStatus.WAITING;

        component().addInputRequest(slot, request);
    }

    public void onRequestedInputPressed() {
        status = AbilityStatus.RUNNING;
    }

    public void clearInputRequest() {
        AbilityComponent component = component();
        if(component.hasInputRequest(slot)) {
            component.removeInputRequest(slot);

            if(status().isWaiting()) {
                status = AbilityStatus.RUNNING;
            }
        }
    }

    private AbilityComponent component() {
        return AbilityComponent.get(player);
    }

    public AbilityType type() {
        return this.type;
    }

    public AbilitySlot slot() {
        return slot;
    }

    public int useTime() {
        return useTime;
    }

    public AbilityStatus status() {
        return status;
    }

    public AbilityInstanceData writeData() {
        return new AbilityInstanceData(type, slot, status, useTime, cooldownTime);
    }

    @Override
    public void saveToNbt(NbtCompound nbt) {
        AbilityInstanceData data = writeData();
        AbilityInstanceData.CODEC.encode(data, NbtOps.INSTANCE, nbt);
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        AbilityInstanceData data = AbilityInstanceData.CODEC.parse(NbtOps.INSTANCE, nbt).getOrThrow();
        this.type = data.type();
        this.slot = data.slot();
        this.status = data.status();
        this.useTime = data.useTime();
        this.cooldownTime = data.cooldownTime();
    }

    @Override
    public void sendToClient() {
        ServerPlayNetworking.send((ServerPlayerEntity) player, new AbilityRuntimeDataSyncS2CPayload(slot, runtimeData.properties()));
    }
}
