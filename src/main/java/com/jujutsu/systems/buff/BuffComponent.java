package com.jujutsu.systems.buff;

import com.jujutsu.mixinterface.EntityComponentsAccessor;
import com.jujutsu.systems.entitydata.ComponentKeys;
import com.jujutsu.systems.entitydata.EntityComponent;
import com.jujutsu.systems.entitydata.EntityTickingComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class BuffComponent implements EntityComponent, EntityTickingComponent {
    private final LivingEntity entity;

    private Map<Identifier, Buff> buffs = new HashMap<>();

    public BuffComponent(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public void tick() {

    }

    public void addBuff(Identifier id, Buff buff) {

    }

    public boolean hasBuff(Identifier id) {
        return buffs.containsKey(id);
    }

    @Override
    public void saveToNbt(NbtCompound nbt) {

    }

    @Override
    public void readFromNbt(NbtCompound nbt) {

    }

    @Override
    public void sendToClient() {

    }

    public static BuffComponent get(LivingEntity entity) {
        return ((EntityComponentsAccessor) entity).jujutsu$getContainer().get(ComponentKeys.BUFFS);
    }
}
