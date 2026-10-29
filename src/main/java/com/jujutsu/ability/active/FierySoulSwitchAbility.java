package com.jujutsu.ability.active;

import com.jujutsu.ability.passive.FierySoulPassiveAbility;
import com.jujutsu.registry.custom.Abilities;
import com.jujutsu.api.ability.core.AbilityInstance;
import com.jujutsu.api.ability.core.AbilityType;
import com.jujutsu.api.ability.passive.PassiveAbility;
import com.jujutsu.api.ability.passive.PassiveAbilityComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Style;

public class FierySoulSwitchAbility extends AbilityType {
    public FierySoulSwitchAbility(int cooldownTime) {
        super(cooldownTime, false);
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        PassiveAbilityComponent component = PassiveAbilityComponent.get(player);

        for(PassiveAbility passiveAbility: component.all()) {
            if(passiveAbility.getType() == Abilities.FIERY_SOUL) {
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
