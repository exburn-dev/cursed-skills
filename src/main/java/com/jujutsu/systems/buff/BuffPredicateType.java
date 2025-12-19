package com.jujutsu.systems.buff;

import com.mojang.serialization.Codec;

public record BuffPredicateType<T extends BuffPredicate>(Codec<T> codec) {
}
