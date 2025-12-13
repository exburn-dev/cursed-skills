package com.jujutsu.systems.ability.data;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbilityProperty<?> property && Objects.equals(property.type(), type()) && Objects.equals(property.name, name);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + type().hashCode();
    }

    public static AbilityProperty<?> fromType(String name, String type) {
        return switch (type) {
            case "int" -> new IntAbilityProperty(name);
            case "double" -> new DoubleAbilityProperty(name);
            default -> new BoolAbilityProperty(name);
        };
    }
}
