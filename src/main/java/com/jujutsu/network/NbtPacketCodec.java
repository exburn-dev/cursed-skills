package com.jujutsu.network;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.Optional;

public class NbtPacketCodec<T> implements PacketCodec<RegistryByteBuf, T> {
    private final Codec<T> codec;

    public NbtPacketCodec(Codec<T> codec) {
        this.codec = codec;
    }

    @Override
    public T decode(RegistryByteBuf buf) {
        NbtElement nbt = PacketCodecs.NBT_ELEMENT.decode(buf);
        Optional<Pair<T, NbtElement>> optional = codec.decode(new Dynamic<>(NbtOps.INSTANCE, nbt)).resultOrPartial();

        return optional.map(Pair::getFirst).orElse(null);
    }

    @Override
    public void encode(RegistryByteBuf buf, T value) {
        Optional<NbtElement> optional = codec.encodeStart(NbtOps.INSTANCE, value).resultOrPartial();
        NbtElement nbt = optional.orElseGet(NbtCompound::new);
        PacketCodecs.NBT_ELEMENT.encode(buf, nbt);
    }
}
