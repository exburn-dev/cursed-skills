package com.jujutsu.systems.buff;

import com.jujutsu.Jujutsu;
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

            var data = dynamic.get("data").get();

            return codec.decode(data.getOrThrow());
        }

        @Override
        public <T> DataResult<T> encode(BuffPredicate buffPredicate, DynamicOps<T> ops, T t) {
            Identifier typeId = JujutsuRegistries.BUFF_PREDICATE_TYPE.getId(buffPredicate.getType());
            Codec<BuffPredicate> codec = (Codec<BuffPredicate>) buffPredicate.getType().codec();

            RecordBuilder<T> builder = ops.mapBuilder();
            builder.add("type", ops.createString(typeId.toString()));
            builder.add("data", codec.encodeStart(ops, buffPredicate));

            return builder.build(t);
        }
    };

    boolean test(LivingEntity entity);
    float getProgress(LivingEntity entity);

    BuffPredicateType<?> getType();
}
