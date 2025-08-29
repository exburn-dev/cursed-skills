package com.jujutsu.systems.ability.upgrade;

import com.mojang.serialization.MapCodec;

public record AbilityUpgradeType<T extends AbilityUpgrade>(MapCodec<T> codec) {

}
