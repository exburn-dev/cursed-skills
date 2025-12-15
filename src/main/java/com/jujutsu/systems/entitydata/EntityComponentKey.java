package com.jujutsu.systems.entitydata;

import net.minecraft.util.Identifier;

public class EntityComponentKey<T extends EntityServerData> {
    private final Identifier id;
    private final Class<T> type;

    public EntityComponentKey(Identifier id, Class<T> type) {
        this.id = id;
        this.type = type;
    }

    public Identifier id() {
        return this.id;
    }

    public Class<T> type() {
        return this.type;
    }
}
