package com.jujutsu.systems.buff;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;

import java.util.List;

public interface BuffHolder {
    Buff getBuff(Identifier id);
    List<Buff> getBuffs();
    void addBuff(Identifier id, Buff buff, EntityAttributeModifier modifier);
    void removeBuff(Identifier id);
}
