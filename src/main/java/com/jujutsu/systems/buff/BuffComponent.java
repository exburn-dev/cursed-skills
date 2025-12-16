package com.jujutsu.systems.buff;

import com.jujutsu.systems.entitydata.EntityComponent;
import com.jujutsu.systems.entitydata.EntityTickingComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

public class BuffComponent implements EntityComponent, EntityTickingComponent {
    private final LivingEntity entity;

    public BuffComponent(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public void tick() {

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
}
