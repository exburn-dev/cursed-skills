package com.jujutsu.systems.buff;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public interface BuffPredicate {
    Codec<BuffPredicate> CODEC = new Codec<BuffPredicate>() {
        @Override
        public <T> DataResult<Pair<BuffPredicate, T>> decode(DynamicOps<T> ops, T t) {
            Dynamic<T> dynamic = new Dynamic<>(ops, t);
            Identifier keyId = Identifier.tryParse(dynamic.get("type").asString(""));
            Codec<BuffPredicate> codec = BuffPredicates.byId(keyId).codec();

            return codec.decode(dynamic);
        }

        @Override
        public <T> DataResult<T> encode(BuffPredicate buffPredicate, DynamicOps<T> ops, T t) {
            Codec<BuffPredicate> codec = (Codec<BuffPredicate>) buffPredicate.getKey().codec();
            ops.set(t, "type", ops.createString(buffPredicate.getKey().id().toString()));

            return codec.encode(buffPredicate, ops, t);
        }
    };

    boolean test(LivingEntity entity);
    float getProgress(LivingEntity entity);

    BuffPredicateKey<?> getKey();
}
