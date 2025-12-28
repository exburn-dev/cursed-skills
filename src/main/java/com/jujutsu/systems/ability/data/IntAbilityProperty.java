package com.jujutsu.systems.ability.data;

import com.mojang.serialization.Codec;

public class IntAbilityProperty extends AbilityProperty<Integer> {
    protected IntAbilityProperty(String name) {
        super(name);
    }

    public static IntAbilityProperty of(String name) {
        return new IntAbilityProperty(name);
    }

    @Override
    public String type() {
        return "int";
    }

    @Override
    public Integer defaultValue() {
        return 0;
    }

    @Override
    public Codec<Integer> getCodec() {
        return Codec.INT;
    }
}
