package com.jujutsu.systems.buff;

import com.jujutsu.registry.JujutsuRegistries;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public interface BuffPredicate {
    Codec<BuffPredicate> CODEC = new Codec<BuffPredicate>() {
        @Override
        public <T> DataResult<Pair<BuffPredicate, T>> decode(DynamicOps<T> ops, T t) {
            Dynamic<T> dynamic = new Dynamic<>(ops, t);
            Identifier typeId = Identifier.tryParse(dynamic.get("type").asString(""));
            Codec<BuffPredicate> codec = (Codec<BuffPredicate>) JujutsuRegistries.BUFF_PREDICATE_TYPE.get(typeId).codec();

            return codec.decode(dynamic);
        }

        @Override
        public <T> DataResult<T> encode(BuffPredicate buffPredicate, DynamicOps<T> ops, T t) {
            Identifier typeId = JujutsuRegistries.BUFF_PREDICATE_TYPE.getId(buffPredicate.getType());
            Codec<BuffPredicate> codec = (Codec<BuffPredicate>) buffPredicate.getType().codec();
            ops.set(t, "type", ops.createString(typeId.toString()));

            return codec.encode(buffPredicate, ops, t);
        }
    };

    boolean test(LivingEntity entity);
    float getProgress(LivingEntity entity);

    BuffPredicateType<?> getType();
}
