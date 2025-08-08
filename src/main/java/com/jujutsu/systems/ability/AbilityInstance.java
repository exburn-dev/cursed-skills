package com.jujutsu.systems.ability;

import com.jujutsu.network.NbtPacketCodec;
import com.jujutsu.registry.JujutsuRegistries;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class AbilityInstance {
    public static final Codec<AbilityInstance> CODEC;
    public static final PacketCodec<RegistryByteBuf, AbilityInstance> PACKET_CODEC;

    private final AbilityType type;
    private AbilityData abilityData;

    private NbtCompound nbt;
    private int useTime;
    private int cooldownTime;
    private boolean isRunning;
    private boolean isCancelled;

    public AbilityInstance(AbilityType type) {
        this.type = type;
        this.nbt = new NbtCompound();
        abilityData = type.getInitialData();
    }

    private AbilityInstance(AbilityType type, AbilityData data, int useTime, int cooldownTime, boolean isRunning, boolean isCancelled) {
        this.type = type;
        this.abilityData = data;
        this.useTime = useTime;
        this.cooldownTime = cooldownTime;
        this.isRunning = isRunning;
        this.isCancelled = isCancelled;
    }

    public void setAbilityData(AbilityData abilityData) {
        this.abilityData = abilityData;
    }

    public <T extends AbilityData> T getAbilityData(Class<T> expectedClass, Supplier<T> fallback) {
        return expectedClass.equals(abilityData.getClass())
                ? expectedClass.cast(abilityData)
                : fallback.get();
    }

    public void start(PlayerEntity player) {
        if(isRunning || cooldownTime > 0) return;
        type.start(player, this);
        isRunning = true;
    }

    public void tick(PlayerEntity player) {
        if(isRunning) {
            type.tick(player, this);
            useTime++;
        }
        else if(cooldownTime > 0) {
            cooldown();
        }
    }

    public void end(PlayerEntity player) {
        if(!isFinished(player) && !type.isCancelable()) return;

        type.end(player, this);
        isRunning = false;
        cooldownTime = type.getCooldownTime(player, this);
    }

    public void onRemoved() {
        useTime = 0;
        isCancelled = false;
    }

    public void cancel() {
        if(type.isCancelable()) {
            this.isCancelled = true;
        }
    }

    public boolean isFinished(PlayerEntity player) {
        return this.type.isFinished(player, this);
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

    public AbilityType getType() {
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
        return String.format("type: %s, cooldown: %s, isRunning: %s, useTime: %s", type.getName().getString(), getCooldownTime(), isRunning, getUseTime());
    }

    static {
        CODEC = new Codec<AbilityInstance>() {
            @Override
            public <T> DataResult<Pair<AbilityInstance, T>> decode(DynamicOps<T> ops, T input) {
                Dynamic<T> dynamic = new Dynamic<>(ops, input);

                Optional<String> typeIdOpt = dynamic.get("type").asString().result();
                if (typeIdOpt.isEmpty()) return DataResult.error(() -> "Missing 'type' field");
                Identifier typeId = Identifier.of(typeIdOpt.get());

                AbilityType type = JujutsuRegistries.ABILITY_TYPE.get(typeId);

                Codec<? extends AbilityData> dataCodec = type.getCodec();
                AbilityData data = dataCodec.parse(dynamic.get("data").orElseEmptyMap())
                        .result()
                        .orElse(null);

                int cooldownTime = dynamic.get("cooldownTime").asInt(0);
                int useTime = dynamic.get("useTime").asInt(0);
                boolean isRunning = dynamic.get("isRunning").asBoolean(false);
                boolean isCancelled = dynamic.get("isCancelled").asBoolean(false);

                return DataResult.success(new Pair<>(new AbilityInstance(type, data, useTime, cooldownTime, isRunning, isCancelled), input));
            }

            @Override
            public <T> DataResult<T> encode(AbilityInstance instance, DynamicOps<T> ops, T t) {
                RecordBuilder<T> builder = ops.mapBuilder();


                Identifier typeId = JujutsuRegistries.ABILITY_TYPE.getId(instance.type);
                if (typeId == null) return DataResult.error(() -> "Unregistered ability type: " + instance.type);

                builder.add("type", ops.createString(typeId.toString()));
                builder.add("cooldownTime", ops.createInt(instance.cooldownTime));
                builder.add("useTime", ops.createInt(instance.useTime));
                builder.add("isRunning", ops.createBoolean(instance.isRunning));
                builder.add("isCancelled", ops.createBoolean(instance.isCancelled));

                Codec<AbilityData> dataCodec = (Codec<AbilityData>) instance.type.getCodec();
                DataResult<T> encodedData = dataCodec.encodeStart(ops, instance.abilityData);
                if (encodedData.result().isPresent()) {
                    builder.add("data", encodedData.result().get());
                }

                return builder.build(t);
            }
        };
//        CODEC = RecordCodecBuilder.create(instance -> instance.group(AbilityType.CODEC.fieldOf("id").forGetter(AbilityInstance::getType),
//                        Codecs.NONNEGATIVE_INT.fieldOf("useTime").forGetter(AbilityInstance::getUseTime),
//                        Codecs.NONNEGATIVE_INT.fieldOf("cooldownTime").forGetter(AbilityInstance::getCooldownTime),
//                        Codec.BOOL.fieldOf("isRunning").forGetter(AbilityInstance::isRunning),
//                        Codec.BOOL.fieldOf("isCancelled").forGetter(AbilityInstance::isCancelled),
//
//                        NbtCompound.CODEC.fieldOf("data").forGetter(AbilityInstance::getNbt))
//                .apply(instance, (type, useTime, cooldownTime, isRunning, isCancelled, nbt) -> {
//                    AbilityInstance instance1 = new AbilityInstance(type);
//                    instance1.useTime = useTime;
//                    instance1.cooldownTime = cooldownTime;
//                    instance1.isRunning = isRunning;
//                    instance1.isCancelled = isCancelled;
//                    instance1.setNbt(nbt);
//                    return instance1;
//                }));

//        PACKET_CODEC =  PacketCodec.tuple(PacketCodecs.registryEntry(JujutsuRegistries.ABILITY_TYPE_REGISTRY_KEY), AbilityInstance::getTypeEntry,
//                PacketCodecs.INTEGER, AbilityInstance::getUseTime,
//                PacketCodecs.INTEGER, AbilityInstance::getCooldownTime,
//                PacketCodecs.BOOL, AbilityInstance::isRunning,
//                PacketCodecs.BOOL, AbilityInstance::isCancelled,
//                PacketCodecs.NBT_COMPOUND, AbilityInstance::getNbt,
//                (type, useTime, cooldownTime, isRunning, isCancelled, nbt) -> {
//                    AbilityInstance instance1 = new AbilityInstance(type);
//                    instance1.useTime = useTime;
//                    instance1.cooldownTime = cooldownTime;
//                    instance1.isRunning = isRunning;
//                    instance1.isCancelled = isCancelled;
//                    instance1.setNbt(nbt);
//                    return instance1;
//                });
        PACKET_CODEC = new NbtPacketCodec<>(CODEC);
    }
}
