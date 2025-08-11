package com.jujutsu.systems.ability.passive;

import com.mojang.serialization.MapCodec;

public record PassiveAbilityType<T extends PassiveAbility>(MapCodec<T> codec) { }
