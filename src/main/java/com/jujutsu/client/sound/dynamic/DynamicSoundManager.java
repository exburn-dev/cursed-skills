package com.jujutsu.client.sound.dynamic;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DynamicSoundManager implements SoundInstanceCallback {
    private static final Minecraft client = Minecraft.getInstance();
    private static DynamicSoundManager instance;

    private final List<AbstractDynamicSoundInstance> activeSounds = new ArrayList<>();

    private DynamicSoundManager() {

    }

    public <T extends AbstractDynamicSoundInstance> void play(T soundInstance) {
        if(activeSounds.contains(soundInstance)) return;

        client.getSoundManager().play(soundInstance);
        activeSounds.add(soundInstance);
    }

    public <T extends AbstractDynamicSoundInstance> void stop(T soundInstance) {
        client.getSoundManager().stop(soundInstance);
        activeSounds.remove(soundInstance);
    }

    public Optional<AbstractDynamicSoundInstance> getPlayingSoundInstance(SoundEvent event) {
        for(AbstractDynamicSoundInstance soundInstance : activeSounds) {
            if(soundInstance.getIdentifier().equals(event.location())) {
                return Optional.of(soundInstance);
            }
        }
        return Optional.empty();
    }

    @Override
    public <T extends AbstractDynamicSoundInstance> void onFinished(T soundInstance) {
        stop(soundInstance);
    }

    public static DynamicSoundManager getInstance() {
        if(instance == null) {
            instance = new DynamicSoundManager();
        }
        return instance;
    }
}
