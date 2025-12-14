package com.jujutsu.systems.entitydata;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;

public interface EntityServerData {
    void sendToClient(RegistryByteBuf buf);
    void saveToNbt(NbtCompound nbt);
}
