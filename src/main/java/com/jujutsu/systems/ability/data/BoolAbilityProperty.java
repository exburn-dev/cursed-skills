package com.jujutsu.systems.ability.data;

import com.mojang.serialization.Codec;

public class BoolAbilityProperty extends AbilityProperty<Boolean> {
    protected BoolAbilityProperty(String name) {
        super(name);
    }

    @Override
    public String type() {
        return "bool";
    }

    @Override
    public Codec<Boolean> getCodec() {
        return Codec.BOOL;
    }

    @Override
    public Boolean defaultValue() {
        return false;
    }

    public static BoolAbilityProperty of(String name) {
        return new BoolAbilityProperty(name);
    }
}
