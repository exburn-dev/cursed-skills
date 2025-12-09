package com.jujutsu.systems.ability.core;

import com.jujutsu.network.NbtPacketCodec;
import com.jujutsu.network.payload.SyncAbilityAdditionalInputPayload;
import com.jujutsu.registry.JujutsuRegistries;
import com.jujutsu.systems.ability.data.AbilityAdditionalInput;
import com.jujutsu.systems.ability.attribute.AbilityAttributeContainerHolder;
import com.jujutsu.systems.ability.attribute.AbilityAttributeModifier;
import com.jujutsu.systems.ability.attribute.AbilityAttributesContainer;
import com.jujutsu.systems.ability.data.AbilityProperty;
import com.jujutsu.systems.ability.task.AbilityTask;
import com.jujutsu.systems.ability.task.TickAbilitiesTask;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class AbilityInstance {
    public static final Codec<AbilityInstance> CODEC;
    public static final PacketCodec<RegistryByteBuf, AbilityInstance> PACKET_CODEC;

    private final AbilityType type;

    private final HashMap<AbilityAdditionalInput, InputData> additionalInput = new HashMap<>();

    private AbilityStatus status = AbilityStatus.NONE;
    private AbilitySlot slot;
    private int useTime;
    private int cooldownTime;
    private boolean syncNeeded;

    private final Map<AbilityProperty<?>, Comparable<?>> runtimeData = new HashMap<>();

    public AbilityInstance(AbilityType type) {
        this.type = type;
        this.slot = null;
    }

    private AbilityInstance(AbilityType type, int useTime, int cooldownTime, AbilityStatus status) {
        this.type = type;
        this.useTime = useTime;
        this.cooldownTime = cooldownTime;
        this.status = status;
    }

    public void start(PlayerEntity player) {
        if(!status.isNone()) return;

        status = AbilityStatus.RUNNING;
        type.start(player, this);
    }

    public void tick(PlayerEntity player) {
        if(status.isRunning()) {
            type.tick(player, this);
            useTime++;
        }
        else if (status.isWaiting()) {
            type.tick(player, this);
            waitingTick(player);
        }
        else if(status.onCooldown()) {
            cooldown();
        }

        if(syncNeeded) {
            TickAbilitiesTask.syncAbilitiesToClient((ServerPlayerEntity) player);
            syncNeeded = false;
        }
    }

    private void waitingTick(PlayerEntity player) {
        if(additionalInput.isEmpty()) {
            this.status = AbilityStatus.RUNNING;
            return;
        }

        for(AbilityAdditionalInput input: additionalInput.keySet()) {
            if(input.timeout() <= 0) continue;
            InputData inputData = additionalInput.get(input);

            if(inputData.timeoutTime >= input.timeout()) {
                if(inputData.timeoutTask != null) {
                    inputData.timeoutTask.execute(player);
                }
                removeAdditionalInput(input);
                syncAdditionalInput(player, true);
                return;
            }

            inputData.timeoutTime++;
        }
    }

    public void tickClient() {
        if(status.onCooldown()) {
            cooldown();
        }
        else if(status.isRunning()) {
            useTime++;
        }
    }

    public void sync() {
        syncNeeded = true;
    }

    public void endAbility(PlayerEntity player) {
        if(!isFinished(player) && !type.isCancelable()) return;

        type.end(player, this);
        status = AbilityStatus.ON_COOLDOWN;
        cooldownTime = type.getCooldownTime(player, this);

        additionalInput.clear();
        syncAdditionalInput(player, true);
    }

    public void endCooldown() {
        useTime = 0;
        status = AbilityStatus.NONE;
    }

    public void cancel() {
        if(type.isCancelable() && status.isRunning()) {
            status = AbilityStatus.CANCELLED;
        }
    }

    public void addDefaultAttributes(PlayerEntity player) {
        if(player.getWorld().isClient()) return;
        AbilityAttributesContainer container = type.getDefaultAttributes();
        AbilityAttributeContainerHolder holder = (AbilityAttributeContainerHolder) player;

        if(container.attributes().isEmpty()) return;
        for(var entry: container.attributes().entrySet()) {
            HashMap<Identifier, AbilityAttributeModifier> holderModifiers = holder.getAbilityAttributes().getModifiers(entry.getKey());
            if(holderModifiers == null) {
                HashMap<Identifier, AbilityAttributeModifier> map = new HashMap<>(entry.getValue());
                holder.getAbilityAttributes().attributes().put(entry.getKey(), map);
            }
            else {
                holderModifiers.putAll(entry.getValue());
            }
        }
    }

    public void addAdditionalInput(PlayerEntity player, AbilityAdditionalInput additionalInput, AbilityTask task, @Nullable AbilityTask timeoutTask) {
        if(!slotInitialized()) return;

        this.additionalInput.put(additionalInput, new InputData(task, timeoutTask));
        this.status = AbilityStatus.WAITING;

        if(player instanceof ServerPlayerEntity) {
            syncAdditionalInput(player, false);
        }
    }

    private void removeAdditionalInput(AbilityAdditionalInput input) {
        additionalInput.remove(input);

        if(additionalInput.isEmpty()) {
            this.status = AbilityStatus.RUNNING;
        }
    }

    private void syncAdditionalInput(PlayerEntity player, boolean clear) {
        ServerPlayNetworking.send((ServerPlayerEntity) player, new SyncAbilityAdditionalInputPayload(this.additionalInput.keySet().stream().toList(), slot, clear));
    }

    public void checkAdditionalInput(PlayerEntity player, AbilityAdditionalInput input) {
        if(this.additionalInput.isEmpty()) return;

        if(additionalInput.containsKey(input)) {
            additionalInput.get(input).task.execute(player);
            removeAdditionalInput(input);
        }
    }

    public <T extends Comparable<T>> void set(AbilityProperty<T> property, T value) {
        this.runtimeData.put(property, value);
    }

    public <T extends Comparable<T>> T get(AbilityProperty<T> property) {
        var optional = getAbilityProperty(property);
        return optional.orElse(null);
    }

    public <T extends Comparable<T>> Optional<T> getAbilityProperty(AbilityProperty<T> property) {
        if(runtimeData.containsKey(property)) {
            return Optional.of( (T) runtimeData.get(property));
        }
        else {
            return Optional.empty();
        }
    }

    public boolean isFinished(PlayerEntity player) {
        return this.type.isFinished(player, this);
    }

    public void cooldown() {
        if(cooldownTime > 0) cooldownTime--;
    }

    public AbilityStatus getStatus() {
        return status;
    }

    public int getUseTime() {
        return this.useTime;
    }

    public int getCooldownTime() {
        return this.cooldownTime;
    }

    public void setCooldownTime(int value) {
        this.cooldownTime = value;
    }

    public AbilityType getType() {
        return this.type;
    }

    public boolean slotInitialized() {
        return this.slot != null;
    }

    public void initializeSlot(AbilitySlot slot) {
        if(slotInitialized()) return;
        this.slot = slot;
    }

    public AbilitySlot getSlot() {
        return this.slot;
    }

    @Override
    public String toString() {
        return String.format("type: %s, cooldown: %s, status: %s, useTime: %s", type.getName().getString(), getCooldownTime(), getStatus(), getUseTime());
    }

    static {
        CODEC = new AbilityInstanceCodec();
        PACKET_CODEC = new NbtPacketCodec<>(CODEC);
    }

    private static class InputData {
        private final AbilityTask task;
        @Nullable
        private final AbilityTask timeoutTask;
        private int timeoutTime = 0;

        private InputData(AbilityTask task, @Nullable AbilityTask timeoutTask) {
            this.task = task;
            this.timeoutTask = timeoutTask;
        }
    }

    private static class AbilityInstanceCodec implements Codec<AbilityInstance> {
        @Override
        public <T> DataResult<Pair<AbilityInstance, T>> decode(DynamicOps<T> ops, T input) {
            Dynamic<T> dynamic = new Dynamic<>(ops, input);

            Optional<String> typeIdOpt = dynamic.get("type").asString().result();
            if (typeIdOpt.isEmpty()) return DataResult.error(() -> "Missing 'type' field");
            Identifier typeId = Identifier.of(typeIdOpt.get());

            AbilityType type = JujutsuRegistries.ABILITY_TYPE.get(typeId);

            int cooldownTime = dynamic.get("cooldownTime").asInt(0);
            int useTime = dynamic.get("useTime").asInt(0);
            AbilityStatus status = AbilityStatus.CODEC.parse(dynamic.get("status").orElseEmptyMap())
                    .result()
                    .orElse(AbilityStatus.NONE);

            return DataResult.success(new Pair<>(new AbilityInstance(type, useTime, cooldownTime, status), input));
        }

        @Override
        public <T> DataResult<T> encode(AbilityInstance instance, DynamicOps<T> ops, T t) {
            RecordBuilder<T> builder = ops.mapBuilder();


            Identifier typeId = JujutsuRegistries.ABILITY_TYPE.getId(instance.type);
            if (typeId == null) return DataResult.error(() -> "Unregistered ability type: " + instance.type);

            builder.add("type", ops.createString(typeId.toString()));
            builder.add("cooldownTime", ops.createInt(instance.cooldownTime));
            builder.add("useTime", ops.createInt(instance.useTime));
            builder.add("status", ops.createInt(instance.status.getId()));

            return builder.build(t);
        }
    }
}
