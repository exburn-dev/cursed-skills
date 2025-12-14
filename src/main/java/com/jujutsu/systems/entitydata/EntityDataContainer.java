package com.jujutsu.systems.entitydata;

import net.minecraft.entity.EntityData;

import java.util.List;

public record EntityDataContainer(List<EntityServerData> dataList) {

    public void tickData() {
        for(EntityServerData data : dataList) {
            if(!(data instanceof EntityTickingComponent tickingComponent)) continue;

            tickingComponent.tick();
        }
    }
}
