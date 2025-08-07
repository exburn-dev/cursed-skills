package com.jujutsu.systems.ability;

import com.jujutsu.registry.JujutsuRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;

public final class AbilityInstance {
    public static final Codec<AbilityInstance> CODEC;
    public static final PacketCodec<RegistryByteBuf, AbilityInstance> PACKET_CODEC;

    private final RegistryEntry<AbilityType> type;

    private NbtCompound nbt;
    private int useTime;
    private int cooldownTime;
    private boolean isRunning;
    private boolean isCancelled;

    public AbilityInstance(RegistryEntry<AbilityType> type) {
        this.type = type;
        this.nbt = new NbtCompound();
        //test
        int a = 123;
        a -= 123;
    }

    public void start(PlayerEntity player) {
        if(isRunning || cooldownTime > 0) return;
        type.value().start(player, this);
        isRunning = true;
    }

    public void tick(PlayerEntity player) {
        if(isRunning) {
            type.value().tick(player, this);
            useTime++;
        }
        else if(cooldownTime > 0) {
            cooldown();
        }
    }

    public void end(PlayerEntity player) {
        if(!isFinished(player) && !type.value().isCancelable()) return;

        type.value().end(player, this);
        isRunning = false;
        cooldownTime = type.value().getCooldownTime(player, this);
    }

    public void onRemoved() {
        useTime = 0;
        isCancelled = false;
    }

    public void cancel() {
        if(type.value().isCancelable()) {
            this.isCancelled = true;
        }
    }

    public boolean isFinished(PlayerEntity player) {
        return this.type.value().isFinished(player, this);
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void cooldown() {
        if(cooldownTime > 0) cooldownTime--;
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

    public RegistryEntry<AbilityType> getType() {
        return this.type;
    }

    public NbtCompound getNbt() {
        return this.nbt;
    }

    private void setNbt(NbtCompound nbt) {
        this.nbt = nbt;
    }

    @Override
    public String toString() {
        return String.format("type: %s, cooldown: %s, isRunning: %s, useTime: %s", type.getKey().get().getValue(), getCooldownTime(), isRunning, getUseTime());
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(AbilityType.ENTRY_CODEC.fieldOf("id").forGetter(AbilityInstance::getType),
                        Codecs.NONNEGATIVE_INT.fieldOf("useTime").forGetter(AbilityInstance::getUseTime),
                        Codecs.NONNEGATIVE_INT.fieldOf("cooldownTime").forGetter(AbilityInstance::getCooldownTime),
                        Codec.BOOL.fieldOf("isRunning").forGetter(AbilityInstance::isRunning),
                        Codec.BOOL.fieldOf("isCancelled").forGetter(AbilityInstance::isCancelled),
                        NbtCompound.CODEC.fieldOf("data").forGetter(AbilityInstance::getNbt))
                .apply(instance, (type, useTime, cooldownTime, isRunning, isCancelled, nbt) -> {
                    AbilityInstance instance1 = new AbilityInstance(type);
                    instance1.useTime = useTime;
                    instance1.cooldownTime = cooldownTime;
                    instance1.isRunning = isRunning;
                    instance1.isCancelled = isCancelled;
                    instance1.setNbt(nbt);
                    return instance1;
                }));

        PACKET_CODEC =  PacketCodec.tuple(PacketCodecs.registryEntry(JujutsuRegistries.ABILITY_TYPE_REGISTRY_KEY), AbilityInstance::getType,
                PacketCodecs.INTEGER, AbilityInstance::getUseTime,
                PacketCodecs.INTEGER, AbilityInstance::getCooldownTime,
                PacketCodecs.BOOL, AbilityInstance::isRunning,
                PacketCodecs.BOOL, AbilityInstance::isCancelled,
                PacketCodecs.NBT_COMPOUND, AbilityInstance::getNbt,
                (type, useTime, cooldownTime, isRunning, isCancelled, nbt) -> {
                    AbilityInstance instance1 = new AbilityInstance(type);
                    instance1.useTime = useTime;
                    instance1.cooldownTime = cooldownTime;
                    instance1.isRunning = isRunning;
                    instance1.isCancelled = isCancelled;
                    instance1.setNbt(nbt);
                    return instance1;
                });
    }
}
