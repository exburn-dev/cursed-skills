package com.jujutsu.client.sound;

import com.jujutsu.systems.ability.client.AbilityClientComponent;
import com.jujutsu.systems.ability.client.ClientComponentContainer;
import com.jujutsu.systems.ability.core.AbilityInstanceData;
import com.jujutsu.systems.ability.core.AbilitySlot;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class HollowPurpleChargingSoundInstance extends MovingSoundInstance {
    private final PlayerEntity player;
    private final AbilitySlot slot;

    public HollowPurpleChargingSoundInstance(PlayerEntity player, AbilitySlot slot, SoundEvent soundEvent, SoundCategory soundCategory) {
        super(soundEvent, soundCategory, SoundInstance.createRandom());
        this.player = player;
        this.slot = slot;

        this.volume = 1f;
        this.pitch = 1f;
        this.repeat = false;

        setPositionToPlayer();
    }

    @Override
    public void tick() {
        AbilityClientComponent component = ClientComponentContainer.abilityComponent;
        AbilityInstanceData instance = component.get(slot);
        if(player == null || player.isRemoved() || player.isDead() || slot == null || instance == null) {
            this.setDone();
            return;
        }

        if(!instance.status().isRunning()) {
            this.setDone();
            return;
        }

        setPositionToPlayer();
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    private void setPositionToPlayer() {
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }
}
