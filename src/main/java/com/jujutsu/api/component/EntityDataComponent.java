package com.jujutsu.api.component;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface EntityDataComponent {
    void save(ValueOutput data);
    void read(ValueInput data);

    void sendToClient();
}
