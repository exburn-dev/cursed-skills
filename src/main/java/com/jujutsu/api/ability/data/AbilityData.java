package com.jujutsu.api.ability.data;

public interface AbilityData {
    AbilityData EMPTY = new NoData();

    record NoData() implements AbilityData { }
}
