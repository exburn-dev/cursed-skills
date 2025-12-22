package com.jujutsu.systems.buff;

import com.jujutsu.client.hud.BuffDisplayData;
import com.jujutsu.mixinterface.EntityComponentsAccessor;
import com.jujutsu.network.payload.buffs.BuffDataSyncS2CPayload;
import com.jujutsu.systems.entitydata.ComponentKeys;
import com.jujutsu.systems.entitydata.EntityComponent;
import com.jujutsu.systems.entitydata.EntityTickingComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuffComponent implements EntityComponent, EntityTickingComponent {
    public static final Codec<Map<Identifier, BuffData>> CODEC;

    private final LivingEntity entity;
    private Map<Identifier, Buff> buffs = new HashMap<>();

    public BuffComponent(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public void tick() {
        for(Identifier id : buffs.keySet()) {
            Buff buff = buffs.get(id);

            buff.tick(id);
        }
    }

    public void addBuff(Identifier id, Buff buff) {
        buffs.put(id, buff);
    }

    public void removeBuff(Identifier id) {
        buffs.remove(id);
    }

    public boolean hasBuff(Identifier id) {
        return buffs.containsKey(id);
    }

    private Map<Identifier, BuffData> buffDataMap() {
        Map<Identifier, BuffData> map = new HashMap<>();

        for(var mapEntry : buffs.entrySet()) {
            map.put(mapEntry.getKey(), mapEntry.getValue().getData());
        }
        return map;
    }

    private void readBuffDataMap(Map<Identifier, BuffData> map) {
        for(var mapEntry : map.entrySet()) {
            Buff buff = new Buff(entity, mapEntry.getValue());
            buffs.put(mapEntry.getKey(), buff);
        }
    }

    @Override
    public void saveToNbt(NbtCompound nbt) {
        NbtCompound compound = new NbtCompound();
        var result = CODEC.encode(buffDataMap(), NbtOps.INSTANCE, nbt);
        if(result.isSuccess()) {
            compound = (NbtCompound) result.getOrThrow();
        }

        nbt.put("Buffs", compound);
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        NbtCompound compound = nbt.getCompound("Buffs");
        var result = CODEC.parse(NbtOps.INSTANCE, compound);
        if(result.isSuccess()) {
            readBuffDataMap(result.getOrThrow());
        }
    }

    @Override
    public void sendToClient() {
        if(!entity.isPlayer()) return;

        List<BuffDisplayData> list = new ArrayList<>();
        for(var mapEntry : buffs.entrySet()) {
            BuffData data = mapEntry.getValue().getData();
            list.add(new BuffDisplayData(data.provider().attribute(), data.conditions().getFirst()));
        }
        ServerPlayNetworking.send((ServerPlayerEntity) entity, new BuffDataSyncS2CPayload(list));
    }

    public static BuffComponent get(LivingEntity entity) {
        return ((EntityComponentsAccessor) entity).jujutsu$getContainer().get(ComponentKeys.BUFFS);
    }

    static {
        CODEC = Codec.unboundedMap(Identifier.CODEC, BuffData.CODEC);
    }
}
