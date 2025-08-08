package com.jujutsu.systems.ability;

public interface AbilityData {
    AbilityData EMPTY = new NoData();

    record NoData() implements AbilityData { }
}
