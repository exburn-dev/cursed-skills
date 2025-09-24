package com.jujutsu.systems.buff;

import com.jujutsu.Jujutsu;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Optional;

public record BuffHashMapStorage(HashMap<Identifier, BuffWrapper> buffs) {
    public static final Codec<BuffHashMapStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Identifier.CODEC, BuffWrapper.CODEC).xmap(HashMap::new, HashMap::new).fieldOf("buffs").forGetter(BuffHashMapStorage::buffs))
            .apply(instance, BuffHashMapStorage::new)
    );

    public <T> T serialize(DynamicOps<T> ops) {
        Optional<T> optional = CODEC.encodeStart(ops, this).resultOrPartial((error) -> {
            Jujutsu.LOGGER.warn("Failed to serialize Buff: {}", error);
        });
        return optional.orElseGet(ops::emptyList);
    }

    public static BuffHashMapStorage deserialize(Dynamic<?> dynamic) {
        var optional = CODEC.decode(dynamic).resultOrPartial((error) -> {
            Jujutsu.LOGGER.warn("Failed to deserialize Buff: {}", error);
        });
        if(optional.isEmpty()) {
            return new BuffHashMapStorage(new HashMap<>());
        }
        return optional.get().getFirst();
    }
}
