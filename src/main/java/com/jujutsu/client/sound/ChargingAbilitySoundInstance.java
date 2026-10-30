package com.jujutsu.client.sound;

import com.jujutsu.api.ability.core.AbilitySlot;
import com.jujutsu.client.sound.dynamic.AbstractDynamicSoundInstance;
import com.jujutsu.sound.dynamic.AbilitySoundSource;
import com.jujutsu.client.sound.dynamic.SoundInstanceCallback;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class ChargingAbilitySoundInstance extends AbstractDynamicSoundInstance {

    public final AbilitySoundSource abilitySoundSource;
    public final AbilitySlot slot;

    public ChargingAbilitySoundInstance(AbilitySoundSource soundSource, SoundEvent soundEvent, SoundSource soundCategory,
                                        int startTransitionTicks, int endTransitionTicks, float maxVolume, float minPitch, float maxPitch,
                                        SoundInstanceCallback callback) {
        super(soundSource, soundEvent, soundCategory, startTransitionTicks, endTransitionTicks, maxVolume, minPitch, maxPitch, callback);

        this.abilitySoundSource = soundSource;
        this.slot = soundSource.instance().slot();
    }

    @Override
    public void tick() {
        if(shouldStop()) {
            finishDynamicSound();
        }
        super.tick();
    }

    @Override
    public void applyModulation() {
        modulateVolumeForStress();
    }

    public boolean shouldStop() {
        return !abilitySoundSource.instance().status().isRunning()
                || abilitySoundSource.player() == null
                || abilitySoundSource.player().isDeadOrDying()
                || abilitySoundSource.player().isRemoved();
    }
}
