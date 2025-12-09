package com.jujutsu.systems.ability.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.Map;

public abstract class AbilityProperty<T extends Comparable<T>> {
    private final String name;
    private final Class<T> type;

    protected AbilityProperty(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public Class<T> type() {
        return this.type;
    }

    public String name() {
        return this.name;
    }

    public abstract Codec<T> getCodec();
}
