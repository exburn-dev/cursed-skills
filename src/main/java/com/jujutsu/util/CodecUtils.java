package com.jujutsu.util;

import com.jujutsu.registry.JujutsuRegistries;
import com.jujutsu.systems.ability.core.AbilityInstance;
import com.jujutsu.systems.ability.core.AbilityStatus;
import com.jujutsu.systems.ability.core.AbilityType;
import com.jujutsu.systems.ability.data.AbilityData;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.util.Identifier;

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
