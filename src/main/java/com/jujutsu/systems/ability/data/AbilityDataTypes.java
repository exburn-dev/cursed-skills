package com.jujutsu.systems.ability.data;

import com.mojang.serialization.Codec;

public class AbilityDataTypes {
    public static final Codec<Double> DOUBLE = Codec.DOUBLE.xmap(Double::new, Double::value);
    public static final Codec<Int> INTEGER = Codec.INT.xmap(Int::new, Int::value);

    public record Double(double value) implements AbilityData {}
    public record Int(int value) implements AbilityData {}
}
