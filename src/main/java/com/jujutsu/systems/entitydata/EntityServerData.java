package com.jujutsu.systems.entitydata;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;

public interface EntityServerData {
    void saveToNbt(NbtCompound nbt);
    void readFromNbt(NbtCompound nbt);

    void sendToClient();
}
