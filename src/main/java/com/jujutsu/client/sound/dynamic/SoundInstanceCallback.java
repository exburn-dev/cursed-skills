package com.jujutsu.client.sound.dynamic;

public interface SoundInstanceCallback {
    <T extends AbstractDynamicSoundInstance> void onFinished(T soundInstance);
}
