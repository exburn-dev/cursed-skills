package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.ability.data.AbilityProperty;
import com.jujutsu.systems.ability.data.BoolAbilityProperty;
import com.jujutsu.systems.ability.data.DoubleAbilityProperty;
import com.jujutsu.systems.ability.data.IntAbilityProperty;
import net.minecraft.registry.Registry;

public class AbilityPropertyTypes {
    public static final Class<IntAbilityProperty> INT_PROPERTY = (Class<IntAbilityProperty>) register(
            "int", IntAbilityProperty.class);

    public static final Class<BoolAbilityProperty> BOOL_PROPERTY = (Class<BoolAbilityProperty>) register(
            "int", BoolAbilityProperty.class);

    public static final Class<DoubleAbilityProperty> DOUBLE_PROPERTY = (Class<DoubleAbilityProperty>) register(
            "int", DoubleAbilityProperty.class);

    //public static <T extends Comparable<T>> AbilityProperty<T> createProperty() {
      //
    //}

    private static <T extends Comparable<T>> Class<? extends AbilityProperty<T>> register(String id, Class<? extends AbilityProperty<T>> clazz) {
        return Registry.register(JujutsuRegistries.ABILITY_PROPERTY, Jujutsu.getId(id), clazz);
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering ability property types for " + Jujutsu.MODID);
    }
}
