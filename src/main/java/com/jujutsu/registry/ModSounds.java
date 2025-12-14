package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public class ModSounds {
    public static final SoundEvent STUN_APPLIED = registerSound("effects.stun_applied", SoundEvent.of(Jujutsu.id("effects.stun_applied")));
    public static final SoundEvent PHOENIX_FIREBALL_CAST = registerSound("abilities.phoenix_fireball_cast", SoundEvent.of(Jujutsu.id("abilities.phoenix_fireball_cast")));
    public static final SoundEvent HIT_IMPACT = registerSound("hit_impact", SoundEvent.of(Jujutsu.id("hit_impact")));
    public static final SoundEvent SHADOW_STEP_CAST = registerSound("abilities.shadow_step_cast", SoundEvent.of(Jujutsu.id("abilities.shadow_step_cast")));
    public static final SoundEvent SHADOW_STEP_END = registerSound("abilities.shadow_step_end", SoundEvent.of(Jujutsu.id("abilities.shadow_step_end")));
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
