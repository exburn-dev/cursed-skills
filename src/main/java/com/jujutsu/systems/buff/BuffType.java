package com.jujutsu.systems.buff;

import com.mojang.serialization.MapCodec;

public record BuffType<T extends IBuff>(MapCodec<T> codec) {
}
