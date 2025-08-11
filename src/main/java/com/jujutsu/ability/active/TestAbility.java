package com.jujutsu.ability.active;

import com.jujutsu.systems.ability.AbilityAdditionalInput;
import com.jujutsu.systems.ability.AbilityInstance;
import com.jujutsu.systems.ability.AbilityType;
import net.minecraft.entity.player.PlayerEntity;

public class TestAbility extends AbilityType {
    public TestAbility(int cooldownTime) {
        super(cooldownTime, true);
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {

    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {
        if(instance.getUseTime() == 60) {
            instance.setAdditionalInput(player, new AbilityAdditionalInput(-1, -1, 1));
        }
    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {

    }

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstance instance) {
        return instance.getUseTime() >= 100;
    }
}
