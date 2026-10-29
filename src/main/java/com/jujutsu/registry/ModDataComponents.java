package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.component.TechniqueComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModDataComponents {
    public static final DataComponentType<TechniqueComponent> TECHNIQUE_COMPONENT = registerComponent("technique",
            new DataComponentType.Builder<TechniqueComponent>()
                    .persistent(TechniqueComponent.CODEC)
                    .networkSynchronized(TechniqueComponent.STREAM_CODEC));

    private static <T> DataComponentType<T> registerComponent(String name, DataComponentType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Jujutsu.id(name), builder.build());
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering data components for " + Jujutsu.MODID);
    }
}
