package com.jujutsu.systems.ability.data;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public abstract class AbilityProperty<T extends Comparable<T>> implements StringIdentifiable {
    private final String name;

    protected AbilityProperty(String name) {
        this.name = name;
    }

    public abstract String type();

    public abstract Codec<T> getCodec();

    public String name() {
        return this.name;
    }

    @Override
    public String asString() {
        return type();
    }
}
