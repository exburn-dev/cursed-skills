package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public class ModSounds {
    public static final SoundEvent HOLLOW_PURPLE_CHARGING = registerSound("abilities.hollow_purple.charging",
            SoundEvent.of(Jujutsu.id("abilities.hollow_purple.charging")));
    public static final SoundEvent HOLLOW_PURPLE_CHARGING_END = registerSound("abilities.hollow_purple.charging_end",
            SoundEvent.of(Jujutsu.id("abilities.hollow_purple.charging_end")));

    public static final SoundEvent SHADOW_STEP_CAST = registerSound("abilities.shadow_step_cast",
            SoundEvent.of(Jujutsu.id("abilities.shadow_step_cast")));
    public static final SoundEvent SHADOW_STEP_END = registerSound("abilities.shadow_step_end",
            SoundEvent.of(Jujutsu.id("abilities.shadow_step_end")));

    public static final SoundEvent PHOENIX_FIREBALL_CAST = registerSound("abilities.phoenix_fireball_cast",
            SoundEvent.of(Jujutsu.id("abilities.phoenix_fireball_cast")));

    public static final SoundEvent SONIC_RIFT_START = registerSound("abilities.sonic_rift.start",
            SoundEvent.of(Jujutsu.id("abilities.sonic_rift.start")));
    public static final SoundEvent SONIC_RIFT_DASH = registerSound("abilities.sonic_rift.dash",
            SoundEvent.of(Jujutsu.id("abilities.sonic_rift.dash")));

    public static final SoundEvent TECHNIQUE_SCROLL_USED = registerSound("item.technique_scroll.used",
            SoundEvent.of(Jujutsu.id("item.technique_scroll.used")));

    public static final SoundEvent STUN_APPLIED = registerSound("effects.stun_applied", SoundEvent.of(Jujutsu.id("effects.stun_applied")));
    public static final SoundEvent HIT_IMPACT = registerSound("hit_impact", SoundEvent.of(Jujutsu.id("hit_impact")));
    public static final SoundEvent UI_HOVER = registerSound("ui_hover", SoundEvent.of(Jujutsu.id("ui_hover")));
    public static final SoundEvent UI_FAILURE = registerSound("ui_failure", SoundEvent.of(Jujutsu.id("ui_failure")));
    public static final SoundEvent UI_SUCCESS = registerSound("ui_success", SoundEvent.of(Jujutsu.id("ui_success")));

    private static SoundEvent registerSound(String name, SoundEvent sound) {
        return Registry.register(Registries.SOUND_EVENT, Jujutsu.id(name), sound);
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering sounds for " + Jujutsu.MODID);
    }
}
