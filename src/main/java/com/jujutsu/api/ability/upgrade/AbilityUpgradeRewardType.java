package com.jujutsu.api.ability.upgrade;

import com.mojang.serialization.MapCodec;

public record AbilityUpgradeRewardType<T extends AbilityUpgradeReward>(MapCodec<T> codec) { }
