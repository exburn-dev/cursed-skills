package com.jujutsu.api.ability.core;

import com.jujutsu.api.ability.task.AbilityWaitTask;
import com.jujutsu.network.payload.abilities.AbilityRuntimeDataSyncS2CPayload;
import com.jujutsu.api.ability.data.AbilityPropertiesContainer;
import com.jujutsu.api.ability.data.AbilityProperty;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayDeque;
import java.util.Queue;

public class AbilityInstance {
    public static final Codec<AbilityInstance> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityInstance> STREAM_CODEC;

    private AbilityType type;
    private AbilitySlot slot;
    private AbilityStatus status;
    private int useTime;
    private int cooldownTime;
    private int maxCooldownTime;

    private AbilityPropertiesContainer runtimeData = new AbilityPropertiesContainer();
    private Queue<AbilityWaitTask> waitTasks = new ArrayDeque<>();

    public AbilityInstance(AbilityType type, AbilitySlot slot) {
        this.type = type;
        this.slot = slot;
        this.status = AbilityStatus.NONE;
    }

    public AbilityInstance(AbilityType type, AbilitySlot slot, AbilityStatus status, int useTime, int cooldownTime, int maxCooldownTime) {
        this.type = type;
        this.slot = slot;
        this.status = status;
        this.useTime = useTime;
        this.cooldownTime = cooldownTime;
        this.maxCooldownTime = maxCooldownTime;
    }

    public void start(Player player) {
        if(!status.isNone()) return;

        status = AbilityStatus.RUNNING;
        type.start(player, this);
    }

    public void tick(Player player) {
        processStatus(player);
    }

    public void endAbility(Player player) {
        if(status.onCooldown()) return;

        boolean isFinished = isFinished(player);
        if(!isFinished && !type.isCancelable()) {
            return;
        }
        else if(!isFinished && type.isCancelable()) {
            status = AbilityStatus.CANCELLED;
        }

        type.end(player, this);
        status = AbilityStatus.ON_COOLDOWN;
        maxCooldownTime = type.getCooldownTime(player, this);
        useTime = 0;
        component(player).sendToClient();
    }

    public void endCooldown() {
        status = AbilityStatus.NONE;
        cooldownTime = 0;
        maxCooldownTime = 0;
        useTime = 0;
    }

    public boolean isFinished(Player player) {
        return type.isFinished(player, this);
    }

    private void processStatus(Player player) {
        if(status.isRunning()) {
            type.tick(player, this);
            if(isFinished(player)) {
                endAbility(player);
            }
            useTime++;
        }
        else if(status.isWaiting()) {
            type.tick(player, this);
            executeWaitTasks(player);
        }
        else if(status.onCooldown()) {
            cooldown();
            if(cooldownTime >= maxCooldownTime) {
                endCooldown();
                component(player).sendToClient();
            }
        }
    }

    private void executeWaitTasks(Player player) {
        AbilityWaitTask current = waitTasks.peek();
        if(current == null) {
            finishWaitingState();
            waitTasks.remove();
            return;
        }

        InteractionResult result = current.tick(player);
        if(isFinishedResult(result)) {
            onWaitTaskFinished(player, current);
            waitTasks.remove();
        }
    }

    private void finishWaitingState() {
        status = AbilityStatus.RUNNING;
    }

    public void awaitTask(AbilityWaitTask task) {
        if(!status.isWaiting()) {
            status = AbilityStatus.WAITING;
        }

        waitTasks.add(task);
    }

    public void onWaitTaskFinished(Player player, AbilityWaitTask task) {
        if(waitTasks.isEmpty()) {
            finishWaitingState();
        }
    }

    public void clearWaitingTasks(Player player) {
        if(!status.isWaiting() || waitTasks.isEmpty()) return;

        for(AbilityWaitTask task : waitTasks) {
            task.finish(player);
        }
        waitTasks.clear();
        finishWaitingState();
    }

    public AbilityWaitTask currentlyAwaitedTask() {
        if(!status.isWaiting()) return null;
        return waitTasks.peek();
    }

    private void cooldown() {
        if(cooldownTime < maxCooldownTime) cooldownTime++;
    }

    public <T extends Comparable<T>> T get(AbilityProperty<T> property, T fallback) {
        return runtimeData.getOrDefault(property, fallback);
    }

    public <T extends Comparable<T>> T get(AbilityProperty<T> property) {
        return get(property, property.defaultValue());
    }

    public <T extends Comparable<T>> void set(AbilityProperty<T> property, T value) {
        runtimeData.set(property, value);
    }

    public void addPropertyValue(AbilityProperty<Integer> property, int toAdd) {
        set(property, get(property, 0) + toAdd);
    }

    private AbilityComponent component(Player player) {
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

    public int cooldownTime() {
        return cooldownTime;
    }

    public int maxCooldownTime() {
        return maxCooldownTime;
    }

    public boolean isExecuted() {
        return status.isRunning() || status.isWaiting();
    }

    @Override
    public void sendToClient() {
        ServerPlayNetworking.send((ServerPlayerEntity) player, new AbilityRuntimeDataSyncS2CPayload(slot, runtimeData.properties()));
    }

    public static boolean isFinishedResult(InteractionResult result) {
        return result == InteractionResult.SUCCESS || result == InteractionResult.FAIL;
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                AbilityType.CODEC.fieldOf("type").forGetter(AbilityInstance::type),
                AbilitySlot.CODEC.fieldOf("slot").forGetter(AbilityInstance::slot),
                AbilityStatus.CODEC.fieldOf("status").forGetter(AbilityInstance::status),
                Codec.INT.fieldOf("useTime").forGetter(AbilityInstance::useTime),
                Codec.INT.fieldOf("cooldownTime").forGetter(AbilityInstance::cooldownTime),
                Codec.INT.fieldOf("maxCooldownTime").forGetter(AbilityInstance::maxCooldownTime)
        ).apply(instance, AbilityInstance::new));
        
        STREAM_CODEC = StreamCodec.composite(
                AbilityType.STREAM_CODEC, AbilityInstance::type,
                AbilitySlot.STREAM_CODEC, AbilityInstance::slot,
                AbilityStatus.STREAM_CODEC, AbilityInstance::status,
                ByteBufCodecs.INT, AbilityInstance::useTime,
                ByteBufCodecs.INT, AbilityInstance::cooldownTime,
                ByteBufCodecs.INT, AbilityInstance::maxCooldownTime,
                AbilityInstance::new
        );
    }
}
