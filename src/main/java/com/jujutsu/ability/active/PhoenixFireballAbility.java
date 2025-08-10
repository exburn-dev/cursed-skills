package com.jujutsu.ability.active;

import com.jujutsu.entity.PhoenixFireballEntity;
import com.jujutsu.registry.ModSounds;
import com.jujutsu.systems.ability.AbilityInstance;
import com.jujutsu.systems.ability.AbilityType;
import com.jujutsu.util.VisualEffectUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;

public class PhoenixFireballAbility extends AbilityType {
    public PhoenixFireballAbility(int cooldownTime) {
        super(cooldownTime, false);
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        if(player.getWorld().isClient()) return;

        PhoenixFireballEntity entity = new PhoenixFireballEntity(player.getWorld(), player.getUuid());
        entity.setPosition(player.getEyePos());
        entity.setYaw(player.getYaw());
        entity.setPitch(player.getPitch());

        player.getWorld().spawnEntity(entity);

        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), RegistryEntry.of(ModSounds.PHOENIX_FIREBALL_CAST), SoundCategory.MASTER, 3, 1, 0);

        VisualEffectUtils.sendScreenFlash((ServerPlayerEntity) player,3, 1, 20, 0.25f, 0xeb4034);
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
