package com.jujutsu.systems.ability;

import com.jujutsu.network.NbtPacketCodec;
import com.jujutsu.network.payload.SyncAbilityAdditionalInputPayload;
import com.jujutsu.registry.JujutsuRegistries;
import com.jujutsu.systems.ability.attribute.AbilityAttribute;
import com.jujutsu.systems.ability.attribute.AbilityAttributeContainerHolder;
import com.jujutsu.systems.ability.attribute.AbilityAttributeModifier;
import com.jujutsu.systems.ability.attribute.AbilityAttributesContainer;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
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
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class AbilityInstance {
    public static final Codec<AbilityInstance> CODEC;
    public static final PacketCodec<RegistryByteBuf, AbilityInstance> PACKET_CODEC;

    private final AbilityType type;
    private AbilityData abilityData;

    private AbilityStatus status = AbilityStatus.NONE;
    private AbilityAdditionalInput additionalInput = null;
    private int useTime;
    private int cooldownTime;

    public AbilityInstance(AbilityType type) {
        this.type = type;
        abilityData = type.getInitialData();
    }

    private AbilityInstance(AbilityType type, AbilityData data, int useTime, int cooldownTime, AbilityStatus status, @Nullable AbilityAdditionalInput additionalInput) {
        this.type = type;
        this.abilityData = data;
        this.useTime = useTime;
        this.cooldownTime = cooldownTime;
        this.status = status;
        this.additionalInput = additionalInput;
    }

    public void start(PlayerEntity player) {
        if(!status.isNone()) return;

        type.start(player, this);
        status = AbilityStatus.RUNNING;
    }

    public void tick(PlayerEntity player) {
        if(status.isRunning()) {
            type.tick(player, this);
            useTime++;
        }
        else if (status.isWaiting()) {
            type.tick(player, this);
        }
        else if(status.onCooldown()) {
            cooldown();
        }
    }

    public void endAbility(PlayerEntity player) {
        if(!isFinished(player) && !type.isCancelable()) return;

        type.end(player, this);
        status = AbilityStatus.ON_COOLDOWN;
        cooldownTime = type.getCooldownTime(player, this);
        additionalInput = null;
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

    public double getAbilityAttributeValue(PlayerEntity player, AbilityAttribute attribute) {
        AbilityAttributeContainerHolder holder = (AbilityAttributeContainerHolder) player;
        List<AbilityAttributeModifier> holderModifiers = new ArrayList<>(holder.getAbilityAttributes().attributes().get(attribute).values());

        holderModifiers.sort(Comparator.comparing(modifier -> modifier.type().getId()));

        double totalValue = 0;
        for(int i = 0; i < holderModifiers.size(); i++) {
            totalValue = holderModifiers.get(i).applyToValue(totalValue);
        }
        return totalValue;
    }

    public void addDefaultAttributes(PlayerEntity player) {
        if(player.getWorld().isClient()) return;
        AbilityAttributesContainer container = type.getDefaultAttributes();
        AbilityAttributeContainerHolder holder = (AbilityAttributeContainerHolder) player;

        if(container.attributes().isEmpty()) return;

        for(var entry: container.attributes().entrySet()) {
            HashMap<Identifier, AbilityAttributeModifier> holderModifiers = holder.getModifiers(entry.getKey());
            if(holderModifiers == null) {
                HashMap<Identifier, AbilityAttributeModifier> map = new HashMap<>(entry.getValue());
                holder.getAbilityAttributes().attributes().put(entry.getKey(), map);
            }
            else {
                holderModifiers.putAll(entry.getValue());
            }
        }
    }

    public void setAdditionalInput(PlayerEntity player, AbilityAdditionalInput additionalInput) {
        if(this.additionalInput == null) {
            this.additionalInput = additionalInput;
            this.status = AbilityStatus.WAITING;

            if(player instanceof ServerPlayerEntity) {
                syncAdditionalInput(player);
            }
        }
    }

    @Nullable
    public AbilityAdditionalInput getAdditionalInput() {
        return this.additionalInput;
    }

    private void syncAdditionalInput(PlayerEntity player) {
        ServerPlayNetworking.send((ServerPlayerEntity) player, new SyncAbilityAdditionalInputPayload(this.additionalInput));
    }

    public boolean checkAdditionalInput(AbilityAdditionalInput additionalInput) {
        if(this.additionalInput == null) return true;
        boolean correctInput = this.additionalInput.keyCode() == additionalInput.keyCode() && this.additionalInput.mouseButton() == additionalInput.mouseButton();
        if(correctInput) {
            this.status = AbilityStatus.RUNNING;
            this.additionalInput = null;
        }

        return correctInput;
    }

    public void setAbilityData(AbilityData abilityData) {
        this.abilityData = abilityData;
    }

    public <T extends AbilityData> T getAbilityData(Class<T> expectedClass, Supplier<T> fallback) {
        return expectedClass.equals(abilityData.getClass())
                ? expectedClass.cast(abilityData)
                : fallback.get();
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

    @Override
    public String toString() {
        return String.format("type: %s, cooldown: %s, status: %s, useTime: %s", type.getName().getString(), getCooldownTime(), getStatus(), getUseTime());
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
                if(data == null) {
                    data = AbilityData.NoData.EMPTY;
                }

                int cooldownTime = dynamic.get("cooldownTime").asInt(0);
                int useTime = dynamic.get("useTime").asInt(0);
                AbilityStatus status = AbilityStatus.CODEC.parse(dynamic.get("status").orElseEmptyMap())
                        .result()
                        .orElse(AbilityStatus.NONE);

                OptionalDynamic<T> additionalInputDynamic = dynamic.get("additionalInput");
                AbilityAdditionalInput additionalInput = null;
                if(additionalInputDynamic.result().isPresent()) {
                    var result = AbilityAdditionalInput.CODEC.decode(additionalInputDynamic.result().get());
                    if(result.result().isPresent()) {
                        additionalInput = result.result().get().getFirst();
                    }
                }

                return DataResult.success(new Pair<>(new AbilityInstance(type, data, useTime, cooldownTime, status, additionalInput), input));
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

                Codec<AbilityData> dataCodec = (Codec<AbilityData>) instance.type.getCodec();
                DataResult<T> encodedData = dataCodec.encodeStart(ops, instance.abilityData);
                if (encodedData.result().isPresent()) {
                    builder.add("data", encodedData.result().get());
                }

                if(instance.additionalInput != null) {
                    DataResult<T> encodedInput = AbilityAdditionalInput.CODEC.encode(instance.additionalInput, ops, t);
                    if(encodedInput.result().isPresent()) {
                        builder.add("additionalInput", encodedInput.result().get());
                    }
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
