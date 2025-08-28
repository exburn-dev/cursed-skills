package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;

public class ModSounds {
    public static final SoundEvent STUN_APPLIED = registerSound("effects.stun_applied", SoundEvent.of(Jujutsu.getId("effects.stun_applied")));
    public static final SoundEvent PHOENIX_FIREBALL_CAST = registerSound("abilities.phoenix_fireball_cast", SoundEvent.of(Jujutsu.getId("abilities.phoenix_fireball_cast")));
    public static final SoundEvent HIT_IMPACT = registerSound("hit_impact", SoundEvent.of(Jujutsu.getId("hit_impact")));
    public static final SoundEvent SHADOW_STEP_CAST = registerSound("abilities.shadow_step_cast", SoundEvent.of(Jujutsu.getId("abilities.shadow_step_cast")));
    public static final SoundEvent SHADOW_STEP_END = registerSound("abilities.shadow_step_end", SoundEvent.of(Jujutsu.getId("abilities.shadow_step_end")));
    public static final SoundEvent UI_HOVER = registerSound("ui_hover", SoundEvent.of(Jujutsu.getId("ui_hover")));
    public static final SoundEvent UI_FAILURE = registerSound("ui_failure", SoundEvent.of(Jujutsu.getId("ui_failure")));
    public static final SoundEvent UI_SUCCESS = registerSound("ui_success", SoundEvent.of(Jujutsu.getId("ui_success")));

    public static final SoundEvent ALLAH_MUSIC_DISC = registerSound("music_disc.allah", SoundEvent.of(Jujutsu.getId("music_disc.allah")));
    public static final SoundEvent SNUS_MUSIC_DISC = registerSound("music_disc.snus", SoundEvent.of(Jujutsu.getId("music_disc.snus")));
    public static final SoundEvent ALLAH_DANGER_MUSIC_DISC = registerSound("music_disc.allah_danger", SoundEvent.of(Jujutsu.getId("music_disc.allah_danger")));

    public static final RegistryKey<JukeboxSong> SNUS_MUSIC_DISC_KEY = RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Jujutsu.getId("snus"));
    public static final RegistryKey<JukeboxSong> ALLAH_MUSIC_DISC_KEY = RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Jujutsu.getId("allah"));

    private static SoundEvent registerSound(String name, SoundEvent sound) {
        return Registry.register(Registries.SOUND_EVENT, Jujutsu.getId(name), sound);
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering sounds for " + Jujutsu.MODID);
    }
}
