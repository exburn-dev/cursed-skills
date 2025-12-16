package com.jujutsu.systems.buff;

import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;

public record BuffPredicateKey<T extends BuffPredicate>(Identifier id, Codec<T> codec) { }
