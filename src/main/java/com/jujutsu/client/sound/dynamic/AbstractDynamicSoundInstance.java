package com.jujutsu.client.sound.dynamic;

import com.jujutsu.sound.dynamic.DynamicSoundSource;
import com.jujutsu.sound.dynamic.TransitionState;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public abstract class AbstractDynamicSoundInstance extends AbstractTickableSoundInstance {
    public final DynamicSoundSource soundSource;
    protected TransitionState transitionState;

    protected final int startTransitionTicks;
    protected final int endTransitionTicks;

    protected final float maxVolume;
    protected final float minPitch;
    protected final float maxPitch;

    protected int currentTick = 0;
    protected int transitionTick = 0;

    protected final SoundInstanceCallback callback;

    public AbstractDynamicSoundInstance(DynamicSoundSource soundSource, SoundEvent soundEvent, SoundSource soundCategory,
                                        int startTransitionTicks, int endTransitionTicks, float maxVolume, float minPitch, float maxPitch,
                                        SoundInstanceCallback callback) {
        super(soundEvent, soundCategory, SoundInstance.createUnseededRandom());

        this.soundSource = soundSource;

        this.startTransitionTicks = startTransitionTicks;
        this.endTransitionTicks = endTransitionTicks;

        this.maxVolume = maxVolume;
        this.minPitch = minPitch;
        this.maxPitch = maxPitch;

        this.callback = callback;
    }

    @Override
    public void tick() {
        if(soundSource == null) {
            finishDynamicSound();
        }

        currentTick++;
        setPositionToEntity();

        switch (this.transitionState) {
            case STARTING -> {
                transitionTick++;

                if(transitionTick > startTransitionTicks) {
                    transitionTick = 0;
                    transitionState = TransitionState.RUNNING;
                }
            }
            case ENDING -> {
                transitionTick++;

                if(transitionTick > endTransitionTicks) {
                    finishDynamicSound();
                }
            }
        }

        applyModulation();
    }

    public abstract void applyModulation();

    public void finishDynamicSound() {
        callback.onFinished(this);
    }

    protected void modulateVolumeForTransition() {
        float normalizedTick = switch (transitionState) {
            case STARTING -> (float) transitionTick / startTransitionTicks;
            case ENDING -> (float) transitionTick / endTransitionTicks;
            default -> 1.0f;
        };

        this.volume = Mth.lerp(normalizedTick, 0.0f, maxVolume);
    }

    protected void modulateVolumeForStress() {
        this.volume = Mth.lerp(soundSource.getNormalizedStress(), 0.0f, maxVolume);
    }

    protected void modulatePitchForStress() {
        pitch = Mth.lerp(soundSource.getNormalizedStress(), minPitch, maxPitch);
    }

    protected void setPositionToEntity() {
        this.x = soundSource.getPosition().x();
        this.y = soundSource.getPosition().y();
        this.z = soundSource.getPosition().z();
    }

    public void end() {
        transitionState = TransitionState.ENDING;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }
}
