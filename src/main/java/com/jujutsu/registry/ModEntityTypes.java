package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModEntityTypes {
    public static final EntityType<HollowPurpleEntity> HOLLOW_PURPLE = registerEntityType("hollow_purple",
            EntityType.Builder.<HollowPurpleEntity>create(HollowPurpleEntity::new, SpawnGroup.MISC).dimensions(0.25f, 0.25f).build());

    public static final EntityType<LapseBlueEntity> LAPSE_BLUE = registerEntityType("lapse_blue",
            EntityType.Builder.<LapseBlueEntity>create(LapseBlueEntity::new, SpawnGroup.MISC).dimensions(0.25f, 0.25f).build());

    public static final EntityType<ReversalRedEntity> REVERSAL_RED = registerEntityType("reversal_red",
            EntityType.Builder.<ReversalRedEntity>create(ReversalRedEntity::new, SpawnGroup.MISC).dimensions(0.25f, 0.25f).build());

    public static final EntityType<BlinkMarkerEntity> BLINK_MARKER = registerEntityType("blink_marker",
            EntityType.Builder.<BlinkMarkerEntity>create(BlinkMarkerEntity::new, SpawnGroup.MISC).dimensions(0.25f, 0.25f).trackingTickInterval(10).build());

    public static final EntityType<PhoenixFireballEntity> PHOENIX_FIREBALL = registerEntityType("phoenix_fireball",
            EntityType.Builder.<PhoenixFireballEntity>create(PhoenixFireballEntity::new, SpawnGroup.MISC).dimensions(0.25f, 0.25f).build());

    private static <T extends Entity> EntityType<T> registerEntityType(String name, EntityType<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, Jujutsu.getId(name), type);
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering entity types for " + Jujutsu.MODID);
    }
}
