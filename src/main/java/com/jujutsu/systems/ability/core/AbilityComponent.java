package com.jujutsu.systems.ability.core;

import com.jujutsu.mixinterface.EntityComponentsAccessor;
import com.jujutsu.network.payload.abilities.AbilitiesSyncS2CPayload;
import com.jujutsu.network.payload.input_requests.ClearInputRequestS2CPayload;
import com.jujutsu.network.payload.input_requests.RequestInputS2CPayload;
import com.jujutsu.registry.ModEffects;
import com.jujutsu.systems.ability.data.InputRequest;
import com.jujutsu.systems.entitydata.*;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
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
    public void onLoaded() {
        for(AbilityInstance instance : abilities.values()) {
            instance.endCooldown();
        }
    }

    @Override
    public void tick() {
        for(AbilityInstance instance : abilities()) {
            instance.tick();
        }
        tickInputRequests();
    }

    public void addInstance(AbilitySlot slot, AbilityType type) {
        AbilityInstance instance = new AbilityInstance(player, type);
        abilities.put(slot, instance);
    }

    public void clearInstances() {
        for(var mapEntry : abilities.entrySet()) {
            mapEntry.getValue().endAbility();
            abilities.remove(mapEntry.getKey());
            //TODO: cancel upgrades effect
        }
    }

    public AbilityInstance getInstance(AbilitySlot slot) {
        return abilities.get(slot);
    }

    public void runAbility(AbilitySlot slot) {
        if(!canStartAbility(slot)) return;
        AbilityInstance instance = abilities.get(slot);
        instance.start();
        sendToClient();
    }

    public boolean canStartAbility(AbilitySlot slot) {
        return abilities.containsKey(slot) && abilities.get(slot).status().isNone() && !player.hasStatusEffect(ModEffects.STUN);
    }

    public Collection<AbilityInstance> abilities() {
        return abilities.values();
    }

    private void tickInputRequests() {
        if(inputRequests.isEmpty()) return;

        for(AbilitySlot slot: inputRequests.keySet()) {
            InputRequest request = inputRequests.get(slot);
            if(request.timeout <= 0) continue;

            if(request.timeoutTime >= request.timeout) {
                request.executeTimeoutTask(player);
                removeInputRequest(slot);
                abilities.get(slot).onRequestedInputPressed();
                continue;
            }

            request.timeoutTime++;
        }
    }

    public void addInputRequest(AbilitySlot slot, InputRequest request) {
        inputRequests.put(slot, request);
        sendInputRequestToClient(slot);
    }

    public void removeInputRequest(AbilitySlot slot) {
        inputRequests.remove(slot);
        sendInputRequestRemoved(slot);

    }

    public boolean hasInputRequest(AbilitySlot slot) {
        return inputRequests.containsKey(slot);
    }

    public void sendInputRequestToClient(AbilitySlot slot) {
        if(!inputRequests.containsKey(slot)) return;
        ServerPlayNetworking.send((ServerPlayerEntity) player, new RequestInputS2CPayload(inputRequests.get(slot).key, slot));
    }

    public void sendInputRequestRemoved(AbilitySlot slot) {
        ServerPlayNetworking.send((ServerPlayerEntity) player, new ClearInputRequestS2CPayload(slot));
    }

    public void receiveRequestedInputPressed(AbilitySlot slot) {
        if(!inputRequests.containsKey(slot)) return;

        AbilityInstance instance = abilities.get(slot);
        InputRequest request = inputRequests.get(slot);

        request.task.execute(player);
        instance.onRequestedInputPressed();
        inputRequests.remove(slot);
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

        abilities.clear();
        var result = CODEC.parse(NbtOps.INSTANCE, compound);
        if(result.isSuccess()) {
            var dataMap = result.getOrThrow();
            for(var mapEntry : dataMap.entrySet()) {
                abilities.put(mapEntry.getKey(), new AbilityInstance(player, mapEntry.getValue()));
            }
        }
    }

    @Override
    public void sendToClient() {
        ServerPlayNetworking.send((ServerPlayerEntity) player, new AbilitiesSyncS2CPayload(
                abilitiesDataMap().values().stream().toList()
        ));
    }

    public static AbilityComponent get(PlayerEntity player) {
        return ((EntityComponentsAccessor) player).jujutsu$getContainer().get(ComponentKeys.ABILITIES);
    }

    static {
        CODEC = Codec.unboundedMap(AbilitySlot.CODEC, AbilityInstanceData.CODEC);
        PACKET_CODEC = PacketCodecs.map(HashMap::new,
                AbilitySlot.PACKET_CODEC, AbilityInstanceData.PACKET_CODEC);
    }
}
