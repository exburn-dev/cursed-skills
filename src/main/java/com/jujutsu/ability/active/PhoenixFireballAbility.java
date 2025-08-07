package com.jujutsu.ability.active;

import com.jujutsu.entity.PhoenixFireballEntity;
import com.jujutsu.registry.ModEntityTypes;
import com.jujutsu.systems.ability.AbilityInstance;
import com.jujutsu.systems.ability.AbilityType;
import net.minecraft.entity.player.PlayerEntity;

public class PhoenixFireballAbility extends AbilityType {
    public PhoenixFireballAbility(int cooldownTime) {
        super(cooldownTime, false);
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        PhoenixFireballEntity entity = new PhoenixFireballEntity(ModEntityTypes.PHOENIX_FIREBALL, player.getWorld());
        entity.setPosition(player.getEyePos());
        entity.setYaw(player.getYaw());
        entity.setPitch(player.getPitch());

        player.getWorld().spawnEntity(entity);
    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {

    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {

    }

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstance instance) {
        return instance.getUseTime() >= 10;
    }
}
