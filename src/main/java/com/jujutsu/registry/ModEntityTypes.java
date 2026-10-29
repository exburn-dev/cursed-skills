package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.entity.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Consumer;

public class ModEntityTypes {
    public static final EntityType<HollowPurpleEntity> HOLLOW_PURPLE = registerMisc("hollow_purple",
            HollowPurpleEntity::new, (b) -> b.sized(0.25f, 0.25f));

    public static final EntityType<LapseBlueEntity> LAPSE_BLUE = registerMisc("lapse_blue",
            LapseBlueEntity::new, (b) -> b.sized(0.25f, 0.25f));

    public static final EntityType<ReversalRedEntity> REVERSAL_RED = registerMisc("reversal_red",
            ReversalRedEntity::new, (b) -> b.sized(0.25f, 0.25f));

    public static final EntityType<BlinkMarkerEntity> BLINK_MARKER = registerMisc("blink_marker",
            BlinkMarkerEntity::new, (b) -> b.sized(0.25f, 0.25f).updateInterval(10));

    public static final EntityType<PhoenixFireballEntity> PHOENIX_FIREBALL = registerMisc("phoenix_fireball",
            PhoenixFireballEntity::new, (b) -> b.sized(1f, 1f));

    private static <T extends Entity> EntityType<T> registerMisc(String name, EntityType.EntityFactory<T> entityFactory,
                                                             Consumer<EntityType.Builder<T>> configuration) {
        return register(name, entityFactory, MobCategory.MISC, configuration);
    }

    private static <T extends Entity> EntityType<T> register(String name, EntityType.EntityFactory<T> entityFactory,
                                                             MobCategory category, Consumer<EntityType.Builder<T>> configuration) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Jujutsu.id(name));

        EntityType.Builder<T> builder = EntityType.Builder.of(entityFactory, category);
        configuration.accept(builder);

        EntityType<T> type = builder.build(key);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type);
        return type;
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering entity types for " + Jujutsu.MODID);
    }
}
