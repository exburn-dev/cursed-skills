package com.jujutsu.systems.ability.data;

import com.mojang.serialization.Codec;

public class DoubleAbilityProperty extends AbilityProperty<Double> {
    protected DoubleAbilityProperty(String name) {
        super(name, Double.class);
    }

    public static DoubleAbilityProperty of(String name) {
        return new DoubleAbilityProperty(name);
    }

    @Override
    public Codec<Double> getCodec() {
        return Codec.DOUBLE;
    }
}
