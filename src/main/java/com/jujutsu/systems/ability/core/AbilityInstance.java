package com.jujutsu.systems.ability.core;

import com.jujutsu.systems.ability.data.AbilityPropertiesContainer;
import com.jujutsu.systems.ability.data.AbilityProperty;
import com.jujutsu.systems.ability.data.InputRequest;
import com.jujutsu.systems.entitydata.EntityServerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;

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

    public AbilityInstance(PlayerEntity player, AbilityType type) {
        this.player = player;
        this.type= type;
    }

    public AbilityInstance(PlayerEntity player, AbilityInstanceData data) {
        this(player, data.type());
        this.slot = data.slot();
        this.status = data.status();
        this.useTime = data.useTime();
        this.cooldownTime = data.cooldownTime();
    }

    public void start() {
        if(!status.isNone()) return;

        status = AbilityStatus.RUNNING;
        //type.start(player, this);
    }

    public void tick() {
        processStatus();
        //TODO: sync
    }

    public void endAbility() {
        if(!(isFinished() || type.isCancelable())) return;

        //type.end(player, this);
        status = AbilityStatus.ON_COOLDOWN;
        //cooldownTime = type.getCooldownTime(player, this);
        useTime = 0;
        //TODO: clear input request
    }

    public void endCooldown() {
        status = AbilityStatus.NONE;
    }

    public boolean isFinished() {
        //return type.isFinished(player, this);
        return true;
    }

    private void processStatus() {
        if(status.isRunning()) {
            //TODO: type be able to work w new AbilityInstance
            //type.tick(player, this);
            useTime++;
        }
        else if(status.isWaiting()) {
            //type.tick(player, this);
            if(AbilityComponent.get(player).hasInputRequest(slot)) {
                status = AbilityStatus.RUNNING;
            }
        }
        else if(status.onCooldown()) {
            cooldown();
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

    private AbilityComponent component() {
        return AbilityComponent.get(player);
    }

    public int useTime() {
        return useTime;
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

    public static AbilityInstance fromNbt(PlayerEntity player, NbtCompound nbt) {
        AbilityInstance instance = new AbilityInstance(player);
        instance.readFromNbt(nbt);
        return instance;
    }

    @Override
    public void sendToClient(RegistryByteBuf buf) {
        AbilityInstanceData.PACKET_CODEC.encode(buf, writeData());
    }
}
