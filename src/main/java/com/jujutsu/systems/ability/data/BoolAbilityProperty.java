package com.jujutsu.systems.ability.data;

import com.mojang.serialization.Codec;

public class BoolAbilityProperty extends AbilityProperty<Boolean> {
    protected BoolAbilityProperty(String name) {
        super(name, Boolean.class);
    }

    public static BoolAbilityProperty of(String name) {
        return new BoolAbilityProperty(name);
    }

    @Override
    public Codec<Boolean> getCodec() {
        return Codec.BOOL;
    }
}
