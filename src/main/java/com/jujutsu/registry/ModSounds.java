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

    public static final SoundEvent ALLAH_MUSIC_DISC = registerSound("music_disc.allah", SoundEvent.of(Jujutsu.getId("music_disc.allah")));
    public static final SoundEvent ALLAH_DANGER_MUSIC_DISC = registerSound("music_disc.allah_danger", SoundEvent.of(Jujutsu.getId("music_disc.allah_danger")));
    public static final RegistryKey<JukeboxSong> ALLAH_MUSIC_DISC_KEY = RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Jujutsu.getId("allah"));

    private static SoundEvent registerSound(String name, SoundEvent sound) {
        return Registry.register(Registries.SOUND_EVENT, Jujutsu.getId(name), sound);
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering sounds for " + Jujutsu.MODID);
    }
}
