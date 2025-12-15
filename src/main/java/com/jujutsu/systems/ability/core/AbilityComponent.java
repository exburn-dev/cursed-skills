package com.jujutsu.systems.ability.core;

import com.jujutsu.systems.entitydata.EntityServerData;
import com.jujutsu.systems.entitydata.EntityTickingComponent;
import com.mojang.serialization.Codec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;

public class AbilityComponent implements EntityServerData, EntityTickingComponent {
    private final PlayerEntity player;
    private final AbilityType type;

    private AbilitySlot slot;
    private AbilityStatus status;
    private int useTime;
    private int cooldownTime;

    public AbilityComponent(PlayerEntity player, AbilityType type) {
        this.player = player;
        this.type = type;
    }

    public AbilityComponent(PlayerEntity player, AbilityComponentData data) {
        this(player, data.type());
        this.slot = data.slot();
        this.status = data.status();
        this.useTime = data.useTime();
        this.cooldownTime = data.cooldownTime();
    }

    @Override
    public void tick() {

    }

    @Override
    public void saveToNbt(NbtCompound nbt) {
        AbilityComponentData data = writeData();
        NbtCompound compound = new NbtCompound();
        AbilityComponentData.CODEC.encode(data, NbtOps.INSTANCE, compound);

        nbt.put("Abilities", compound);
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {

    }

    @Override
    public void sendToClient(RegistryByteBuf buf) {

    }

    public AbilityComponentData writeData() {
        return new AbilityComponentData(type, slot, status, useTime, cooldownTime);
    }

    static {

    }
}
