package com.jujutsu.systems.ability.core;

import com.jujutsu.systems.ability.data.InputRequest;
import com.jujutsu.systems.entitydata.EntityComponent;
import com.jujutsu.systems.entitydata.EntityServerData;
import com.jujutsu.systems.entitydata.EntityTickingComponent;
import com.mojang.serialization.Codec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.HashMap;
import java.util.Map;

public class AbilityComponent implements EntityComponent, EntityTickingComponent {
    public static final Codec<Map<AbilitySlot, AbilityInstanceData>> CODEC;
    public static final PacketCodec<RegistryByteBuf, Map<AbilitySlot, AbilityInstanceData>> PACKET_CODEC;

    private final PlayerEntity player;

    private final Map<AbilitySlot, AbilityInstance> abilities = new HashMap<>();
    private final Map<AbilitySlot, InputRequest> inputRequests = new HashMap<>();

    public AbilityComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void tick() {

    }

    public void addInputRequest(AbilitySlot slot, InputRequest request) {
        inputRequests.put(slot, request);
    }

    public boolean hasInputRequest(AbilitySlot slot) {
        return inputRequests.containsKey(slot);
    }

    private Map<AbilitySlot, AbilityInstanceData> abilitiesDataMap() {
        Map<AbilitySlot, AbilityInstanceData> map = new HashMap<>();
        for(var mapEntry : abilities.entrySet()) {
            map.put(mapEntry.getKey(), mapEntry.getValue().writeData());
        }
        return map;
    }

    @Override
    public void saveToNbt(NbtCompound nbt) {
        NbtCompound compound = new NbtCompound();
        CODEC.encode(abilitiesDataMap(), NbtOps.INSTANCE, compound);

        nbt.put("Abilities", compound);
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        NbtCompound compound = nbt.getCompound("Abilities");

        var dataMap = CODEC.parse(NbtOps.INSTANCE, compound).getOrThrow();
        abilities.clear();
        for(var mapEntry : dataMap.entrySet()) {
            abilities.put(mapEntry.getKey(), new AbilityInstance(player, mapEntry.getValue()));
        }
    }

    @Override
    public void sendToClient(RegistryByteBuf buf) {
        PACKET_CODEC.encode(buf, abilitiesDataMap());
    }

    public static AbilityComponent get(PlayerEntity player) {
        //TODO;
        return null;
    }

    static {
        CODEC = Codec.unboundedMap(AbilitySlot.CODEC, AbilityInstanceData.CODEC);
        PACKET_CODEC = PacketCodecs.map(HashMap::new,
                AbilitySlot.PACKET_CODEC, AbilityInstanceData.PACKET_CODEC);
    }
}
