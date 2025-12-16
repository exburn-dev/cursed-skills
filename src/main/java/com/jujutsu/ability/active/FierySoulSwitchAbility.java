package com.jujutsu.ability.active;

import com.jujutsu.ability.passive.FierySoulPassiveAbility;
import com.jujutsu.registry.ModAbilities;
import com.jujutsu.systems.ability.core.AbilityInstance;
import com.jujutsu.systems.ability.core.AbilityType;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
import com.jujutsu.systems.ability.passive.PassiveAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Style;

public class FierySoulSwitchAbility extends AbilityType {
    public FierySoulSwitchAbility(int cooldownTime) {
        super(cooldownTime, false);
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        IAbilitiesHolder holder = (IAbilitiesHolder) player;

        for(PassiveAbility passiveAbility: holder.getPassiveAbilities()) {
            if(passiveAbility.getType() == ModAbilities.FIERY_SOUL) {
                FierySoulPassiveAbility fierySoul = (FierySoulPassiveAbility) passiveAbility;

                fierySoul.setEnabled(!fierySoul.isEnabled());
            }
        }
    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {

    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {

    }

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstance instance) {
        return instance.useTime() >= 5;
    }

    @Override
    public Style getStyle() {
        return Style.EMPTY.withColor(0x858585);
    }
}
