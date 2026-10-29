package com.jujutsu.api.buff;

import com.jujutsu.api.component.EntityDataComponent;
import com.jujutsu.client.hud.BuffDisplayData;
import com.jujutsu.mixinterface.EntityComponentsAccessor;
import com.jujutsu.network.payload.buffs.BuffDataSyncS2CPayload;
import com.jujutsu.api.component.ComponentKeys;
import com.jujutsu.api.component.EntityComponent;
import com.jujutsu.api.component.EntityTickingComponent;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.*;

public class BuffComponent implements EntityComponent, EntityTickingComponent, EntityDataComponent {
    public static final Codec<Map<Identifier, BuffData>> CODEC;

    private final LivingEntity entity;
    private Map<Identifier, Buff> buffs = new HashMap<>();

    private final Set<Identifier> toRemove = new HashSet<>();

    public BuffComponent(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public void tick() {
        for(Identifier id : buffs.keySet()) {
            Buff buff = buffs.get(id);

            buff.tick(id);
        }

        toRemove.forEach(this::removeBuff);
        toRemove.clear();
    }

    public void addBuff(Identifier id, Buff buff) {
        buffs.put(id, buff);
    }

    public void markForRemoval(Identifier id) {
        toRemove.add(id);
    }

    public boolean hasBuff(Identifier id) {
        return buffs.containsKey(id);
    }

    private void removeBuff(Identifier id) {
        if(!buffs.containsKey(id)) return;
        buffs.get(id).provider().remove(entity, id);
        buffs.remove(id);
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
    public void save(ValueOutput data) {
        data.store("Buffs", CODEC, buffDataMap());
    }

    @Override
    public void read(ValueInput data) {
        var result = data.read("Buffs", CODEC);
        if(result.isEmpty()) return;

        readBuffDataMap(result.get());
    }

    @Override
    public void sendToClient() {
        if(!(entity instanceof Player player)) return;

        List<BuffDisplayData> list = new ArrayList<>();
        for(var mapEntry : buffs.entrySet()) {
            BuffData data = mapEntry.getValue().getData();
            list.add(new BuffDisplayData(data.provider().attribute(), data.conditions().getFirst()));
        }
        ServerPlayNetworking.send((ServerPlayer) player, new BuffDataSyncS2CPayload(list));
    }

    public static BuffComponent get(LivingEntity entity) {
        return ((EntityComponentsAccessor) entity).jujutsu$getContainer().get(ComponentKeys.BUFFS);
    }

    static {
        CODEC = Codec.unboundedMap(Identifier.CODEC, BuffData.CODEC);
    }
}
