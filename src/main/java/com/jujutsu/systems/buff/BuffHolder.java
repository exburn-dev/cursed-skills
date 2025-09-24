package com.jujutsu.systems.buff;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;

import java.util.List;

public interface BuffHolder {
    BuffWrapper getBuff(Identifier id);
    List<BuffWrapper> getBuffs();
    void addBuff(Identifier id, BuffWrapper buffWrapper);
    void removeBuff(Identifier id);
}
