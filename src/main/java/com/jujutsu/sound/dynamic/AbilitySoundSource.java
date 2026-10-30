package com.jujutsu.sound.dynamic;

import com.jujutsu.api.ability.core.AbilityInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class AbilitySoundSource implements DynamicSoundSource {
    private final AbilityInstance instance;
    private final Player player;

    public AbilitySoundSource(AbilityInstance instance, Player player) {
        this.instance = instance;
        this.player = player;
    }

    public AbilityInstance instance() {
        return instance;
    }

    public Player player() {
        return player;
    }

    @Override
    public int getTick() {
        return instance.useTime();
    }

    @Override
    public Vec3 getPosition() {
        return player.position();
    }

    @Override
    public float getNormalizedStress() {
        return instance.type().getProgress(player, instance);
    }
}
