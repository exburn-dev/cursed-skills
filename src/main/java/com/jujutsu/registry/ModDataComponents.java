package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.component.TechniqueComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModDataComponents {
    public static final ComponentType<TechniqueComponent> TECHNIQUE_COMPONENT = registerComponent("technique",
            new ComponentType.Builder<TechniqueComponent>().codec(TechniqueComponent.CODEC).packetCodec(TechniqueComponent.PACKET_CODEC));

    private static <T> ComponentType<T> registerComponent(String name, ComponentType.Builder<T> builder) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Jujutsu.id(name), builder.build());
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering data components for " + Jujutsu.MODID);
    }
}
