package com.jujutsu.api.buff;

import com.mojang.serialization.Codec;

public record BuffConditionType<T extends BuffCondition>(Codec<T> codec) {
}
