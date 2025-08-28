package com.jujutsu.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CodecUtils {
    public static <T, O> O serialize(Codec<T> codec, DynamicOps<O> ops, T input, Consumer<String> errorConsumer) {
        Optional<O> optional = codec.encodeStart(ops, input).resultOrPartial(errorConsumer);
        return optional.orElseGet(ops::emptyList);
    }

    public static <T> T deserialize(Codec<T> codec, Dynamic<?> dynamic, Supplier<T> createEmpty, Consumer<String> errorConsumer) {
        var optional = codec.decode(dynamic).resultOrPartial(errorConsumer);
        if(optional.isEmpty()) {
            return createEmpty.get();
        }
        return optional.get().getFirst();
    }
}
