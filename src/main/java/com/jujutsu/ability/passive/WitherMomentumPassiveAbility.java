package com.jujutsu.ability.passive;

import com.jujutsu.registry.custom.Abilities;
import com.jujutsu.api.ability.passive.PassiveAbility;
import com.jujutsu.api.ability.passive.PassiveAbilityType;
import com.mojang.serialization.MapCodec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Style;

public class WitherMomentumPassiveAbility extends PassiveAbility {
    public static final MapCodec<WitherMomentumPassiveAbility> CODEC = MapCodec.unit(new WitherMomentumPassiveAbility());

    //Marker-ability. Realisation in ServerEventListeners GET_SPEED_BONUS event listener

    public WitherMomentumPassiveAbility() {}

    @Override
    public void tick(PlayerEntity player) {}

    @Override
    public void onGained(PlayerEntity player) {

    }

    @Override
    public void onRemoved(PlayerEntity player) {

    }

    @Override
    public Style getStyle() {
        return Style.EMPTY.withColor(0x4a385c);
    }

    @Override
    public PassiveAbilityType<?> getType() {
        return Abilities.WITHER_MOMENTUM;
    }
}
