package com.jujutsu.api.component;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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

    public void writeNbt(ValueOutput data) {
        for(EntityComponent component : all()) {
            if(component instanceof EntityDataComponent dataComponent) {
                dataComponent.save(data);
            }
        }
    }

    public void readNbt(ValueInput data) {
        for(EntityComponent component : all()) {
            if(component instanceof EntityDataComponent dataComponent) {
                dataComponent.read(data);
            }
        }
    }
}
