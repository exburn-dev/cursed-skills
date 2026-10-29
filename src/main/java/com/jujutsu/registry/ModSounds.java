package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static final SoundEvent HOLLOW_PURPLE_CHARGING = register("abilities.hollow_purple.charging");
    public static final SoundEvent HOLLOW_PURPLE_CHARGING_END = register("abilities.hollow_purple.charging_end");

    public static final SoundEvent SHADOW_STEP_CAST = register("abilities.shadow_step_cast");
    public static final SoundEvent SHADOW_STEP_END = register("abilities.shadow_step_end");

    public static final SoundEvent PHOENIX_FIREBALL_CAST = register("abilities.phoenix_fireball_cast");

    public static final SoundEvent SONIC_RIFT_START = register("abilities.sonic_rift.start");
    public static final SoundEvent SONIC_RIFT_DASH = register("abilities.sonic_rift.dash");

    public static final SoundEvent TECHNIQUE_SCROLL_USED = register("item.technique_scroll.used");

    public static final SoundEvent STUN_APPLIED = register("effects.stun_applied");
    public static final SoundEvent HIT_IMPACT = register("hit_impact");
    public static final SoundEvent UI_HOVER = register("ui_hover");
    public static final SoundEvent UI_FAILURE = register("ui_failure");
    public static final SoundEvent UI_SUCCESS = register("ui_success");

    private static SoundEvent register(String name) {
        Identifier id = Jujutsu.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    private static SoundEvent registerFixed(String name, float range) {
        Identifier id = Jujutsu.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createFixedRangeEvent(id, range));
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering sounds for " + Jujutsu.MODID);
    }
}
