package com.jujutsu.systems.ability.data;

import com.mojang.serialization.Codec;

public class IntAbilityProperty extends AbilityProperty<Integer> {
    protected IntAbilityProperty(String name) {
        super(name, Integer.class);
    }

    public static IntAbilityProperty of(String name) {
        return new IntAbilityProperty(name);
    }

    @Override
    public Codec<Integer> getCodec() {
        return Codec.INT;
    }
}
