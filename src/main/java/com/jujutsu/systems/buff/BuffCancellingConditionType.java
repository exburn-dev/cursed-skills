package com.jujutsu.systems.buff;

import com.mojang.serialization.MapCodec;

public record BuffCancellingConditionType<T extends BuffCancellingCondition>(MapCodec<T> codec) { }
