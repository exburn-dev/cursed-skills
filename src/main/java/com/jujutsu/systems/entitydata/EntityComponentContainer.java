package com.jujutsu.systems.entitydata;

import net.minecraft.nbt.NbtCompound;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EntityComponentContainer {
    private final Map<EntityComponentKey<?>, EntityComponent> components = new HashMap<>();

    public <T extends EntityComponent> void add(EntityComponentKey<T> key, T component) {
        components.put(key, component);
    }

    @SuppressWarnings("unchecked")
    public <T extends EntityComponent> T get(EntityComponentKey<T> key) {
        return (T) components.get(key);
    }

    public Collection<EntityComponent> all() {
        return components.values();
    }

    public void tick() {
        for(EntityComponent component : all()) {
            if(!(component instanceof EntityTickingComponent tickingComponent)) continue;
            tickingComponent.tick();
        }
    }

    public NbtCompound writeNbt() {
        NbtCompound nbt = new NbtCompound();
        for(EntityComponent component : all()) {
            component.saveToNbt(nbt);
        }
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        for(EntityComponent component : all()) {
            component.readFromNbt(nbt);
        }
    }
}
