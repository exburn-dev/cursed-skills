package com.jujutsu.systems.ability.data;

import com.mojang.serialization.Codec;

public class DoubleAbilityProperty extends AbilityProperty<Double> {
    protected DoubleAbilityProperty(String name) {
        super(name);
    }

    @Override
    public String type() {
        return "double";
    }

    @Override
    public Codec<Double> getCodec() {
        return Codec.DOUBLE;
    }

    @Override
    public Double defaultValue() {
        return 0.0;
    }

    public static DoubleAbilityProperty of(String name) {
        return new DoubleAbilityProperty(name);
    }


}
