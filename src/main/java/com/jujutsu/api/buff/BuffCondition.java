package com.jujutsu.api.buff;

import com.jujutsu.registry.JujutsuRegistries;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

public interface BuffCondition {
    Codec<BuffCondition> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<BuffCondition, T>> decode(DynamicOps<T> ops, T t) {
            Dynamic<T> dynamic = new Dynamic<>(ops, t);
            Identifier typeId = Identifier.tryParse(dynamic.get("type").asString(""));
            Codec<BuffCondition> codec = (Codec<BuffCondition>) JujutsuRegistries.BUFF_PREDICATE_TYPE.getValue(typeId).codec();

            var data = dynamic.get("data").get();

            return codec.decode(data.getOrThrow());
        }

        @Override
        public <T> DataResult<T> encode(BuffCondition buffCondition, DynamicOps<T> ops, T t) {
            Identifier typeId = JujutsuRegistries.BUFF_PREDICATE_TYPE.getKey(buffCondition.getType());
            Codec<BuffCondition> codec = (Codec<BuffCondition>) buffCondition.getType().codec();

            RecordBuilder<T> builder = ops.mapBuilder();
            builder.add("type", ops.createString(typeId.toString()));
            builder.add("data", codec.encodeStart(ops, buffCondition));

            return builder.build(t);
        }
    };

    boolean test(LivingEntity entity);
    float getProgress(LivingEntity entity);

    BuffConditionType<?> getType();
}
